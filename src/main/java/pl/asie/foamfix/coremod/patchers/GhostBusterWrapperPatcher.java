package pl.asie.foamfix.coremod.patchers;

import com.google.common.collect.Sets;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import pl.asie.foamfix.bugfixmod.coremod.MappingRegistry;
import pl.asie.foamfix.bugfixmod.coremod.patchers.AbstractPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ModificationPatcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class GhostBusterWrapperPatcher extends AbstractPatcher implements ModificationPatcher {
	private static Map<String, String> operationsMap;

	public GhostBusterWrapperPatcher(String name, String targetClassName, String targetMethodName, String targetMethodDesc) {
		super(name, targetClassName, targetMethodName, targetMethodDesc);

		if (operationsMap == null) {
			operationsMap = new HashMap<>();
			operationsMap.put("net/minecraft/world/IBlockAccess." + MappingRegistry.getMethodNameFor("IBlockAccess.getBlock"), "getBlock;(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/block/Block;");
			operationsMap.put("net/minecraft/world/World." + MappingRegistry.getMethodNameFor("IBlockAccess.getBlock"), "getBlock;(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/block/Block;");
			operationsMap.put("net/minecraft/world/WorldServer." + MappingRegistry.getMethodNameFor("IBlockAccess.getBlock"), "getBlock;(Lnet/minecraft/world/IBlockAccess;III)Lnet/minecraft/block/Block;");
			operationsMap.put("net/minecraft/world/IBlockAccess." + MappingRegistry.getMethodNameFor("IBlockAccess.isAirBlock"), "isAirBlock;(Lnet/minecraft/world/IBlockAccess;III)Z");
			operationsMap.put("net/minecraft/world/World." + MappingRegistry.getMethodNameFor("IBlockAccess.isAirBlock"), "isAirBlock;(Lnet/minecraft/world/IBlockAccess;III)Z");
			operationsMap.put("net/minecraft/world/WorldServer." + MappingRegistry.getMethodNameFor("IBlockAccess.isAirBlock"), "isAirBlock;(Lnet/minecraft/world/IBlockAccess;III)Z");
		}
	}

	@Override
	public String getPatcherName() {
		return patcherName + "/" + targetMethodName;
	}

	@Override
	public InsnList buildNewInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet) {
		return null;
	}

	@Override
	public void modifyInsns(AbstractInsnNode currentInstruction, Iterator<AbstractInsnNode> instructionSet, InsnList instructions) {
		if (currentInstruction instanceof MethodInsnNode) {
			String value = operationsMap.get(((MethodInsnNode) currentInstruction).owner + "." + ((MethodInsnNode) currentInstruction).name);
			if (value != null) {
				String[] valueSplit = value.split(";", 2);

				printMessage("Applying wrapper to " + ((MethodInsnNode) currentInstruction).name + "!");
				((MethodInsnNode) currentInstruction).owner = "pl/asie/foamfix/ghostbuster/GhostBusterSafeAccessors";
				((MethodInsnNode) currentInstruction).name = valueSplit[0];
				((MethodInsnNode) currentInstruction).desc = valueSplit[1];
				((MethodInsnNode) currentInstruction).itf = false;
				((MethodInsnNode) currentInstruction).setOpcode(Opcodes.INVOKESTATIC);
				successful = true;
			}
		}
	}
}
