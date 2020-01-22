function initializeCoreMod() {
    return {
        'setLootTable': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.tileentity.LockableLootTileEntity',
                'methodName': 'func_195479_a',
                'methodDesc': '(Lnet/minecraft/world/IBlockReader;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;)V'
            },
            'transformer': function (method) {
                print("[LootR] Patching LockableLootTileEntity::setLootTable (Static)");

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 3));
                insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "noobanidus/mods/lootr/tiles/LockableTileReplacement", "setLootTable", "(Lnet/minecraft/world/IBlockReader;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;)V", false));
                insn.add(new InsnNode(Opcodes.RETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}