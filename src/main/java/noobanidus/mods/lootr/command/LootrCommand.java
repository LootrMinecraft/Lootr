package noobanidus.mods.lootr.command;

import com.google.common.collect.ImmutableList;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.LootrChestBlock;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.entity.LootrChestMinecartEntity;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.util.ChestUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static net.minecraft.command.CommandBase.parseCoordinate;

public class LootrCommand implements ICommand {
    private static List<ResourceLocation> tables = null;
    private static List<String> tableNames = null;
    private static final Map<String, UUID> profileMap = new HashMap<>();

    @Override
    @Nonnull
    public String getName() {
        return "lootr";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender iCommandSender) {
        return "lootr.commands.usage";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return ImmutableList.of();
    }

    @Nullable
    private static String getArgIfExists(String[] args, int i) {
        if(args.length >= (i+1))
            return args[i];
        return null;
    }

    @Nonnull
    private static String getArgAt(String[] args, int i) throws CommandException {
        if(args.length >= (i+1))
            return args[i];
        throw new WrongUsageException("lootr.commands.usage");
    }

    private void clearPlayerProfile(ICommandSender c, String profileName) {
        GameProfile profile = c.getServer().getPlayerProfileCache().getGameProfileForUsername(profileName);
        if (profile == null) {
            c.sendMessage(new TextComponentString("Invalid player name: " + profileName + ", profile not found in the cache."));
            return;
        }
        c.sendMessage(new TextComponentString(DataStorage.clearInventories(profile.getId()) ? "Cleared stored inventories for " + profileName : "No stored inventories for " + profileName + " to clear"));
    }

    private static Block getBlockFromArg(String arg) {
        switch (arg) {
            case "chest":
                return ModBlocks.CHEST;
            case "cart":
                return null;
            case "shulker":
                return ModBlocks.SHULKER;
            default:
                throw new IllegalArgumentException(arg);
        }
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        String command = getArgAt(args, 0);
        switch(command) {
            case "clear":
                clearPlayerProfile(sender, getArgAt(args, 1));
                break;
            case "chest":
            case "cart":
            case "shulker": {
                String table = getArgIfExists(args, 1);
                createBlock(sender, getBlockFromArg(command), table != null ? new ResourceLocation(table) : null);
                break;
            }
            case "openers": {
                if(args.length < 4)
                    throw new WrongUsageException("lootr.commands.usage");
                Vec3d vec3d = sender.getPositionVector();
                int j = 1;
                CommandBase.CoordinateArg argX = parseCoordinate(vec3d.x, args[j++], true);
                CommandBase.CoordinateArg argY = parseCoordinate(vec3d.y, args[j++], -4096, 4096, false);
                CommandBase.CoordinateArg argZ = parseCoordinate(vec3d.z, args[j++], true);
                BlockPos position = new BlockPos(argX.getResult(), argY.getResult(), argZ.getResult());
                World world = sender.getEntityWorld();
                TileEntity tile = world.getTileEntity(position);
                if (tile instanceof ILootTile) {
                    Set<UUID> openers = ((ILootTile) tile).getOpeners();
                    sender.sendMessage(new TextComponentString("Tile at location " + position + " has " + openers.size() + " openers. UUIDs as follows:"));
                    for (UUID uuid : openers) {
                        GameProfile profile = sender.getServer().getPlayerProfileCache().getProfileByUUID(uuid);
                        sender.sendMessage(new TextComponentString("UUID: " + uuid + ", user profile: " + (profile == null ? "null" : profile.getName())));
                    }
                } else {
                    sender.sendMessage(new TextComponentString("No Lootr tile exists at location: " + position));
                }
                break;
            }
            case "custom" : {
                BlockPos pos = sender.getPosition();
                World world = sender.getEntityWorld();
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() != Blocks.CHEST) {
                    pos = pos.down();
                    state = world.getBlockState(pos);
                }
                if (state.getBlock() != Blocks.CHEST) {
                    sender.sendMessage(new TextComponentString("Please stand on the chest you wish to convert."));
                } else {
                    NonNullList<ItemStack> reference = ((TileEntityChest) Objects.requireNonNull(world.getTileEntity(pos))).chestContents;
                    NonNullList<ItemStack> custom = ChestUtil.copyItemList(reference);
                    world.removeTileEntity(pos);
                    world.setBlockState(pos, ModBlocks.INVENTORY.getDefaultState().withProperty(BlockChest.FACING, state.getValue(BlockChest.FACING)));
                    TileEntity te = world.getTileEntity(pos);
                    if (!(te instanceof LootrInventoryTileEntity)) {
                        sender.sendMessage(new TextComponentString("Unable to convert chest, BlockState is not a Lootr Inventory block."));
                    } else {
                        LootrInventoryTileEntity inventory = (LootrInventoryTileEntity) te;
                        inventory.setCustomInventory(custom);
                        inventory.markDirty();
                    }
                }
                break;
            }
            default:
                throw new WrongUsageException("lootr.commands.usage");
        }
    }

    @Override
    public boolean checkPermission(@Nonnull MinecraftServer server, ICommandSender iCommandSender) {
        return iCommandSender.canUseCommand(2, getName());
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, String[] args, @Nullable BlockPos blockPos) {
        if(args.length == 0)
            return ImmutableList.of("chest", "shulker", "cart", "clear", "openers");
        String cmd = args[0];
        if(cmd.equals("chest") || cmd.equals("shulker") || cmd.equals("cart"))
            return getTableNames();
        return ImmutableList.of();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int i) {
        return args.length >= 1 && args[0].equals("clear") && i == 1;
    }

    @Override
    public int compareTo(@Nonnull ICommand iCommand) {
        return 0;
    }

    private static List<ResourceLocation> getTables() {
        if (tables == null) {
            tables = new ArrayList<>(LootTableList.getAll());
            tableNames = tables.stream().map(ResourceLocation::toString).collect(Collectors.toList());
        }
        return tables;
    }

    private static List<String> getTableNames() {
        getTables();
        return tableNames;
    }

    public static void createBlock(ICommandSender c, @Nullable Block block, @Nullable ResourceLocation table) {
        World world = c.getEntityWorld();
        BlockPos pos = new BlockPos(c.getPosition());
        if (table == null) {
            table = getTables().get(world.rand.nextInt(getTables().size()));
        }
        if (block == null) {
            LootrChestMinecartEntity cart = new LootrChestMinecartEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            Entity e = c.getCommandSenderEntity();
            if (e != null) {
                cart.rotationYaw = e.rotationYaw;
            }
            cart.setLootTable(table, world.rand.nextLong());
            world.spawnEntity(cart);
            c.sendMessage(new TextComponentTranslation("lootr.commands.summon", new TextComponentTranslation("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(new Style().setColor(TextFormatting.GREEN)), table.toString()));
        } else {
            IBlockState placementState = block.getDefaultState();
            Entity e = c.getCommandSenderEntity();
            if (e != null) {
                PropertyEnum<EnumFacing> prop = null;
                EnumFacing dir = null;
                if (placementState.getPropertyKeys().contains(LootrChestBlock.FACING)) {
                    prop = LootrChestBlock.FACING;
                    dir = e.getHorizontalFacing().getOpposite();
                } else if (placementState.getPropertyKeys().contains(LootrShulkerBlock.FACING)) {
                    prop = LootrShulkerBlock.FACING;
                }
                if (prop != null && dir != null) {
                    placementState = placementState.withProperty(prop, dir);
                }
            }
            world.setBlockState(pos, placementState, 2);
            ((TileEntityLockableLoot)world.getTileEntity(pos)).setLootTable(table, world.rand.nextLong());
            c.sendMessage(new TextComponentTranslation("lootr.commands.create", new TextComponentString(block.getLocalizedName()), new TextComponentTranslation("lootr.commands.blockpos", pos.getX(), pos.getY(), pos.getZ()).setStyle(new Style().setColor(TextFormatting.GREEN)), table.toString()));
        }
    }
}
