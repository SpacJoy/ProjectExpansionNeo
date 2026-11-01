package cool.furry.mc.neoforge.projectexpansion.registries;

/**
 * Placeholder for the block type registry.
 * <p>
 * 26.3 removed the {@code minecraft:block_type} registry together with {@code BlockBehaviour#codec} and
 * {@code BlockBehaviour#propertiesCodec}: block properties are no longer serialized, so a block never needs a
 * {@code MapCodec} any more. The class is kept as an empty shell so the registration call in {@code Main} and the
 * references in the block classes keep resolving; the individual {@code BLOCK} constants are gone with the registry.
 */
public class BlockTypes {
}