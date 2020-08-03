function initializeCoreMod() {
    return {
        'getInventory': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.tileentity.LockableLootTileEntity',
                'methodName': 'func_189404_a',
                'methodDesc': '(Lnet/minecraft/util/ResourceLocation;J)V'
            },
            'transformer': function (method) {
                print("[LootR] Patching LockableLootTileEntity::setLootTable");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestUtil", "setLootTable", "(Lnet/minecraft/tileentity/LockableLootTileEntity;Lnet/minecraft/util/ResourceLocation;)V", ASMAPI.MethodType.STATIC));
                insn.add(new InsnNode(Opcodes.RETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}