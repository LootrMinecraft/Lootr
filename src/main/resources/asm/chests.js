function initializeCoreMod() {
    return {
        'drops': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ChestBlock',
                'methodName': 'func_196283_a_',
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;)Lnet/minecraft/tileentity/TileEntity;'
            },
            'transformer': function (method) {
                print("[LootR] Patching ChestBlock::createNewTileEntity...");

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var TypeInsnNode = Java.type('org.objectweb.asm.tree.TypeInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new TypeInsnNode(Opcodes.NEW, "noobanidus/mods/lootr/tiles/SpecialLootChestTile"));
                insn.add(new InsnNode(Opcodes.DUP));
                insn.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "noobanidus/mods/lootr/tiles/SpecialLootChestTile", "<init>", "()V", false));
                insn.add(new InsnNode(Opcodes.ARETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}