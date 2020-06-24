function initializeCoreMod() {
    return {
        'generateChest': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.world.gen.feature.structure.StructurePiece',
                'methodName': 'func_191080_a',
                'methodDesc': '(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Z'
            },
            'transformer': function (method) {
                print("[LootR] Patching StructurePiece::generateChest");

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
                insn.add(new VarInsnNode(Opcodes.ALOAD, 3));
                insn.add(new VarInsnNode(Opcodes.ILOAD, 4));
                insn.add(new VarInsnNode(Opcodes.ILOAD, 5));
                insn.add(new VarInsnNode(Opcodes.ILOAD, 6));
                insn.add(ASMAPI.buildMethodCall("noobanidus/mods/lootr/world/CorridorReplacement", "generateChest", "(Lnet/minecraft/world/gen/feature/structure/StructurePiece;Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/MutableBoundingBox;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/block/BlockState;)Z", ASMAPI.MethodType.STATIC));
                insn.add(new InsnNode(Opcodes.IRETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}
