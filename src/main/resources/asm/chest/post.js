function initializeCoreMod() {
    return {
        'updatePostPlacement': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ChestBlock',
                'methodName': 'func_196271_a',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/Direction;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;'
            },
            'transformer': function (method) {
                print("[LootR] Patching ChestBlock::updatePostPlacement");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var instr = method.instructions;

                var jumpNode = null;
                var i;
                var count = false;
                for (i = 0; i < instr.size(); i++) {
                    var n = instr.get(i);
                    if (n.getOpcode() == Opcodes.ARETURN) {
                        if (count != false) {
                            jumpNode = n.getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious().getPrevious();
                            break;
                        } else {
                            count = true;
                        }
                    }
                }

                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 4));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 5));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestUtil", "isLootChest", "(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z", ASMAPI.MethodType.STATIC));
                insn.add(new JumpInsnNode(Opcodes.IFEQ, jumpNode));
                instr.insert(insn);

                return method;
            }
        }
    }
}
