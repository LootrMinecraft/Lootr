function initializeCoreMod() {
    return {
        'onReplaced': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.BarrelBlock',
                'methodName': 'func_196243_a',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V'
            },
            'transformer': function (method) {
                print("[LootR] Patching BarrelBlock::onReplaced");

                var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var instr = method.instructions;

                var jumpNode = null;
                var i;
                var count = 0;
                for (i = 0; i < instr.size(); i++) {
                    var n = instr.get(i);
                    if (n.getOpcode() == Opcodes.ALOAD) {
                        if (count == 2) {
                            jumpNode = n;
                            break;
                        }
                        count++;
                    }
                }

                var insn = new InsnList();

                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 3));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 4));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestUtil", "handleLootChestReplaced", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;)V", ASMAPI.MethodType.STATIC));
                instr.insertBefore(jumpNode, insn);

                return method;
            }
        }
    }
}
