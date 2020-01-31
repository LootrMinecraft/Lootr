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
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var instr = method.instructions;

                var jumpNode = null;
                var i;
                for (i = 0; i < instr.size(); i++) {
                    var n = instr.get(i);
                    if (n.getOpcode() == Opcodes.ACONST_NULL) {
                        jumpNode = n.getPrevious().getPrevious();
                        break;
                    }
                }

                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestUtil", "isLootChest", "(Lnet/minecraft/item/BlockItemUseContext;Lnet/minecraft/util/Direction;)Z", ASMAPI.MethodType.STATIC));
                insn.add(new JumpInsnNode(Opcodes.IFEQ, jumpNode));
                instr.insert(insn);

                return method;
            }
        }
    }
}
