package noobanidus.mods.lootr.event;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.FolderName;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.ContainerData;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.data.DecayData;
import noobanidus.mods.lootr.data.PlayerData;
import noobanidus.mods.lootr.data.Statistics;
import noobanidus.mods.lootr.data.old.AdvancementData;
import noobanidus.mods.lootr.data.old.ChestData;
import noobanidus.mods.lootr.data.old.TickingData;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleData
{
	private static final Set<UUID> MARK_FOR_REMOVAL = new ObjectOpenHashSet<>();
	
	@SubscribeEvent
	public static void onWorldLoaded(WorldEvent.Load event)
	{
		IWorld world = event.getWorld();
		if(world instanceof ServerWorld)
		{
			ServerWorld server = (ServerWorld)world;
			if(server.dimension() == World.OVERWORLD)
			{
				switch(migrateFiles(server.getServer().getWorldPath(new FolderName("data")), server.getServer().getWorldPath(FolderName.PLAYER_DATA_DIR).toFile(), server))
				{
					case SUCCESS:
						Lootr.LOG.info("Successfully Migrated Data to new System");
						break;
					case FAIL:
						Lootr.LOG.error("Couldn't migrate data to new System");
						break;
					default:
						break;
				}
			}
		}
	}
	
	public static ActionResultType migrateFiles(Path path, File playerPath, ServerWorld world)
	{
		List<String> ids = new ArrayList<>();
		try(Stream<Path> paths = Files.walk(path))
		{
			paths.forEach(o -> {
				if(Files.isRegularFile(o))
				{
					String name = o.getFileName().toString();
					if(name.startsWith("Lootr-") && !name.endsWith("Data.dat"))
					{
						ids.add(name.replace(".dat", ""));
					}
				}
			});
		}
		catch(IOException e)
		{
			return ActionResultType.FAIL;
		}
		if(ids.isEmpty()) return ActionResultType.PASS;
		Map<UUID, PlayerData> migrationMap = new Object2ObjectLinkedOpenHashMap<>();
		Function<UUID, PlayerData> playerData = T -> loadData(new File(playerPath, "lootR_" + T.toString() + ".dat"));
		BiFunction<UUID, UUID, ContainerData> containerGetter = (P, T) -> migrationMap.computeIfAbsent(P, playerData).getOrCreate(T, () -> new ContainerData());
		Function<UUID, Statistics> statsGetter = T -> migrationMap.computeIfAbsent(T, playerData).getStats();
		DimensionSavedDataManager manager = world.getDataStorage();
		long time = world.getGameTime();
		ids.forEach(id -> migrate(manager, path, id, ChestData::new, T -> T.migrate(containerGetter)));
		migrate(manager, path, "Lootr-AdvancementData", AdvancementData::new, T -> T.migrateAwards(statsGetter));
		migrate(manager, path, "Lootr-ScoreData", AdvancementData::new, T -> T.migrateScore(statsGetter));
		migrate(manager, path, "Lootr-RefreshData", TickingData::new, T -> T.migrateRefresh(Lists.transform(new ObjectArrayList<>(migrationMap.values()), PlayerData::getTimedData), time));
		migrate(manager, path, "Lootr-DecayData", TickingData::new, T -> T.migrateDecay(manager.computeIfAbsent(DecayData::getDecay, "Lootr-Decay-Data"), time));
		for(Map.Entry<UUID, PlayerData> entry : migrationMap.entrySet())
		{
			saveData(playerPath, entry.getKey().toString(), entry.getValue().save(time));
		}
		
		return ActionResultType.SUCCESS;
	}
	
	private static <T extends WorldSavedData> void migrate(DimensionSavedDataManager manager, Path path, String id, Function<String, T> creator, Consumer<T> migrator) {
		if(Files.notExists(path.resolve(id+".dat"))) return;
		try
		{
			T created = creator.apply(id);
			created.load(manager.readTagFromDisk(id, SharedConstants.getCurrentVersion().getWorldVersion()).getCompound("data"));
			migrator.accept(created);
			Files.deleteIfExists(path.resolve(id+".dat"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoaded(PlayerEvent.LoadFromFile event)
	{
		try
		{
			File file = new File(event.getPlayerDirectory(), "lootR_" + event.getPlayerUUID() + ".dat");
			if(file.exists() && file.isFile())
			{
				DataStorage.DATA.put(event.getPlayer().getUUID(), loadData(file));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			DataStorage.DATA.put(event.getPlayer().getUUID(), new PlayerData());
		}
	}
	
	@SubscribeEvent
	public static void onPlayerSaved(PlayerEvent.SaveToFile event)
	{
		UUID id = event.getPlayer().getUUID();
		CompoundNBT data = DataStorage.DATA.getOrDefault(id, new PlayerData()).save(event.getPlayer().level.getGameTime());
		saveData(event.getPlayerDirectory(), event.getPlayerUUID(), data);
		if(MARK_FOR_REMOVAL.contains(id))
		{
			DataStorage.DATA.remove(data);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
	{
		MARK_FOR_REMOVAL.add(event.getPlayer().getUUID());
	}
	
	private static void saveData(File file, String playerUUID, CompoundNBT data) {
		try
		{
			File temp = File.createTempFile("lootR_" + playerUUID + "-", "_.dat", file);
			CompressedStreamTools.writeCompressed(data, temp);
			File current = new File(file, "lootR_" + playerUUID + ".dat");
			File old = new File(file, "lootR_" + playerUUID + ".dat_old");
			Util.safeReplaceFile(current, temp, old);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static PlayerData loadData(File file) {
		if(file.exists() && file.isFile())
		{
			try
			{
				return new PlayerData().load(CompressedStreamTools.readCompressed(file));
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		return new PlayerData();
	}
}
