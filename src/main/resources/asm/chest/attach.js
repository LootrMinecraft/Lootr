function initializeCoreMod() {
    return {
        'getDirectionToAttach': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ChestBlock',
                'methodName': 'func_196312_a',
                'methodDesc': '(Lnet/minecraft/item/BlockItemUseContext;Lnet/minecraft/util/Direction;)Lnet/minecraft/util/Direction;'
            },
            'transformer': function (method) {
                print("[LootR] Patching ChestBlock::getDirectionToAttach");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');

                var instr = method.instructions;

                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/blocks/ChestBlockReplacement", "getDirectionToAttach", "(Lnet/minecraft/block/ChestBlock;Lnet/minecraft/item/BlockItemUseContext;Lnet/minecraft/util/Direction;)Lnet/minecraft/util/Direction;", ASMAPI.MethodType.STATIC));
                insn.add(new InsnNode(Opcodes.ARETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}
