function initializeCoreMod() {
    return {
        'onBlockActivated': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.BarrelBlock',
                'methodName': 'func_220051_a',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/math/BlockRayTraceResult;)Z'
            },
            'transformer': function (method) {
                print("[LootR] Patching BarrelBlock::onBlockActivated");

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
                    if (n.getOpcode() == Opcodes.IRETURN) {
                        jumpNode = n.getPrevious().getPrevious().getPrevious();
                        break;
                    }
                }

                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 3));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 4));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/util/ChestUtil", "handleLootChest", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/player/PlayerEntity;)Z", ASMAPI.MethodType.STATIC));
                insn.add(new JumpInsnNode(Opcodes.IFNE, jumpNode));
                instr.insert(insn);

                return method;
            }
        }
    }
}
