package noobanidus.mods.lootr.gen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class LootrDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(LootrBlockTagProvider::new);
        pack.addProvider(LootrItemTagsProvider::new);
        pack.addProvider(LootrLootTableProvider::new);
    }
}
