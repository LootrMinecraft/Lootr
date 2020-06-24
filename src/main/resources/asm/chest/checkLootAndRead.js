function initializeCoreMod() {
    return {
        'getInventory': {
            'target': {
                'type': 'METHOD',
                'class': ' net.minecraft.tileentity.LockableLootTileEntity',
                'methodName': 'func_184283_b',
                'methodDesc': '(Lnet/minecraft/nbt/CompoundNBT;)Z'
            },
            'transformer': function (method) {
                print("[LootR] Patching LockableLootTileEntity::checkLootAndRead");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ILOAD, 3));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestBlockReplacement", "getInventory", "(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/inventory/IInventory;", ASMAPI.MethodType.STATIC));
                insn.add(new InsnNode(Opcodes.ARETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}