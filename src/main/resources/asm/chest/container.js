function initializeCoreMod() {
    return {
        'getInventory': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ChestBlock',
                'methodName': 'func_220052_b',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/container/INamedContainerProvider;'
            },
            'transformer': function (method) {
                print("[LootR] Patching ChestBlock::getContainer");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 3));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/blocks/ChestBlockReplacement", "getContainer", "(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/inventory/container/INamedContainerProvider;", ASMAPI.MethodType.STATIC));
                insn.add(new InsnNode(Opcodes.ARETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}