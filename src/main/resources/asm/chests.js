function initializeCoreMod() {
    return {
        'getInventory': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.ChestBlock',
                'methodName': 'func_220105_a',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/inventory/IInventory;'
            },
            'transformer': function (method) {
                print("[LootR] Patching static ChestBlock::getInventory");

                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var MethodInsnNode = Java.type('org.objectweb.asm.tree.MethodInsnNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var FieldInsnNode = Java.type('org.objectweb.asm.tree.FieldInsnNode');

                var instr = method.instructions;
                var insn = new InsnList();
                insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
                insn.add(new VarInsnNode(Opcodes.ALOAD, 2));
                insn.add(new VarInsnNode(Opcodes.ILOAD, 3));
                insn.add(new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraft/block/ChestBlock", "field_220109_i", "Lnet/minecraft/block/ChestBlock$InventoryFactory;"));
                insn.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "noobanidus/mods/lootr/blocks/ChestBlockReplacement", "getInventory", "(Lnet/minecraft/block/ChestBlock;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;ZLnet/minecraft/block/ChestBlock$InventoryFactory;)Lnet/minecraft/inventory/IInventory;", false));
                insn.add(new InsnNode(Opcodes.ARETURN));
                instr.insert(insn);

                return method;
            }
        }
    }
}