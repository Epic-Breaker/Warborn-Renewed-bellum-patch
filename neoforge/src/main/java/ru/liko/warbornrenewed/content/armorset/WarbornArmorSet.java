package ru.liko.warbornrenewed.content.armorset;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.liko.warbornrenewed.registry.ModItems;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

public final class WarbornArmorSet {
    private final String id;
    private final EnumMap<ArmorItem.Type, List<DeferredItem<WarbornArmorItem>>> piecesByType;
    private final List<DeferredItem<WarbornArmorItem>> allPieces;

    private WarbornArmorSet(String id,
            EnumMap<ArmorItem.Type, List<DeferredItem<WarbornArmorItem>>> piecesByType,
            List<DeferredItem<WarbornArmorItem>> allPieces) {
        this.id = id;
        EnumMap<ArmorItem.Type, List<DeferredItem<WarbornArmorItem>>> byTypeCopy = new EnumMap<>(ArmorItem.Type.class);
        piecesByType.forEach((type, list) -> byTypeCopy.put(type, List.copyOf(list)));
        this.piecesByType = byTypeCopy;
        this.allPieces = List.copyOf(allPieces);
    }

    public String id() {
        return id;
    }

    public Collection<DeferredItem<WarbornArmorItem>> pieces() {
        return allPieces;
    }

    public Optional<DeferredItem<WarbornArmorItem>> piece(ArmorItem.Type type) {
        return Optional.ofNullable(piecesByType.get(type))
                .flatMap(list -> list.stream().findFirst());
    }

    public List<DeferredItem<WarbornArmorItem>> pieces(ArmorItem.Type type) {
        List<DeferredItem<WarbornArmorItem>> list = piecesByType.get(type);
        return list == null ? List.of() : list;
    }

    static WarbornArmorSet register(DeferredRegister.Items items, Builder builder) {
        EnumMap<ArmorItem.Type, List<DeferredItem<WarbornArmorItem>>> registrations = new EnumMap<>(
                ArmorItem.Type.class);
        List<DeferredItem<WarbornArmorItem>> all = new ArrayList<>();
        builder.definitions.forEach((type, definitions) -> {
            List<DeferredItem<WarbornArmorItem>> entries = registrations.computeIfAbsent(type,
                    key -> new ArrayList<>());
            for (ArmorPieceDefinition definition : definitions) {
                DeferredItem<WarbornArmorItem> entry = items.register(definition.registryName(),
                        () -> definition.create(type));
                entries.add(entry);
                all.add(entry);
                ModItems.trackArmorPiece(entry);
            }
        });
        return new WarbornArmorSet(builder.id, registrations, all);
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static final class Builder {
        private final String id;
        private final Map<ArmorItem.Type, List<ArmorPieceDefinition>> definitions = new EnumMap<>(ArmorItem.Type.class);
        private final Map<ArmorItem.Type, Integer> typeCounts = new EnumMap<>(ArmorItem.Type.class);
        private ArmorPieceDefinition defaults;

        private Builder(String id) {
            this.id = Objects.requireNonNull(id, "id");
        }

        public Builder defaultMaterial(ArmorPieceDefinition.MaterialProvider material) {
            defaults = ensureDefaults().withMaterial(material);
            return this;
        }

        public Builder defaultVisuals(Consumer<ArmorVisualSpec.Builder> visuals) {
            ArmorVisualSpec.Builder builder = ArmorVisualSpec.builder();
            visuals.accept(builder);
            defaults = ensureDefaults().withVisuals(builder.build());
            return this;
        }

        public Builder defaultBones(UnaryOperator<ArmorBonesSpec.Builder> operator) {
            ArmorBonesSpec.Builder bonesBuilder = operator.apply(ArmorBonesSpec.builder());
            defaults = ensureDefaults().withBones(bonesBuilder.build());
            return this;
        }

        public Builder helmet(Consumer<ArmorPieceBuilder> consumer) {
            return piece(ArmorItem.Type.HELMET, consumer);
        }

        public Builder chestplate(Consumer<ArmorPieceBuilder> consumer) {
            return piece(ArmorItem.Type.CHESTPLATE, consumer);
        }

        public Builder leggings(Consumer<ArmorPieceBuilder> consumer) {
            return piece(ArmorItem.Type.LEGGINGS, consumer);
        }

        public Builder boots(Consumer<ArmorPieceBuilder> consumer) {
            return piece(ArmorItem.Type.BOOTS, consumer);
        }

        public WarbornArmorSet register(DeferredRegister.Items items) {
            int totalPieces = definitions.values().stream().mapToInt(List::size).sum();
            if (totalPieces == 0) {
                throw new IllegalStateException("No armor pieces configured for set " + id);
            }
            definitions.values().stream()
                    .flatMap(List::stream)
                    .forEach(definition -> definition.validate(id));
            return WarbornArmorSet.register(items, this);
        }

        private Builder piece(ArmorItem.Type type, Consumer<ArmorPieceBuilder> consumer) {
            int sequenceIndex = typeCounts.compute(type, (key, count) -> count == null ? 1 : count + 1) - 1;
            ArmorPieceBuilder builder = new ArmorPieceBuilder(id, type, defaults, sequenceIndex);
            consumer.accept(builder);
            ArmorPieceDefinition definition = builder.build();
            definitions.computeIfAbsent(type, key -> new ArrayList<>()).add(definition);
            return this;
        }

        private ArmorPieceDefinition ensureDefaults() {
            if (defaults == null) {
                defaults = ArmorPieceDefinition.defaults();
            }
            return defaults;
        }
    }

    public static final class ArmorPieceDefinition {
        private final String registryName;
        private final MaterialProvider materialProvider;
        private final ArmorVisualSpec visuals;
        private final ArmorBonesSpec bones;
        private final PropertiesProvider propertiesProvider;
        private final List<ArmorAttributeSpec> attributes;
        private final List<String> visionCapabilities;
        private final boolean dyeable;

        ArmorPieceDefinition(String registryName, MaterialProvider materialProvider, ArmorVisualSpec visuals,
                ArmorBonesSpec bones, PropertiesProvider propertiesProvider, List<ArmorAttributeSpec> attributes,
                List<String> visionCapabilities) {
                this(registryName, materialProvider, visuals, bones, propertiesProvider, attributes, visionCapabilities, false);
            }

            ArmorPieceDefinition(String registryName, MaterialProvider materialProvider, ArmorVisualSpec visuals,
                ArmorBonesSpec bones, PropertiesProvider propertiesProvider, List<ArmorAttributeSpec> attributes,
                List<String> visionCapabilities, boolean dyeable) {
            this.registryName = registryName;
            this.materialProvider = materialProvider;
            this.visuals = visuals;
            this.bones = bones;
            this.propertiesProvider = propertiesProvider;
            this.attributes = attributes;
            this.visionCapabilities = visionCapabilities;
            this.dyeable = dyeable;
        }

        static ArmorPieceDefinition defaults() {
            return new ArmorPieceDefinition(null, null, null, null, PropertiesProvider.identity(), List.of(),
                    List.of());
        }

        ArmorPieceDefinition withMaterial(MaterialProvider material) {
            return new ArmorPieceDefinition(registryName, material, visuals, bones, propertiesProvider, attributes,
                    visionCapabilities);
        }

        ArmorPieceDefinition withVisuals(ArmorVisualSpec visuals) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    attributes, visionCapabilities);
        }

        ArmorPieceDefinition withBones(ArmorBonesSpec bones) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    attributes, visionCapabilities);
        }

        ArmorPieceDefinition withProperties(PropertiesProvider propertiesProvider) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    attributes, visionCapabilities);
        }

        ArmorPieceDefinition withRegistryName(String registryName) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    attributes, visionCapabilities);
        }

        ArmorPieceDefinition withAttributes(List<ArmorAttributeSpec> attributes) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    List.copyOf(attributes), visionCapabilities);
        }

        ArmorPieceDefinition withVisionCapabilities(List<String> visionCapabilities) {
            return new ArmorPieceDefinition(registryName, materialProvider, visuals, bones, propertiesProvider,
                    attributes, List.copyOf(visionCapabilities));
        }

        void validate(String setId) {
            if (visuals == null) {
                throw new IllegalStateException("Visuals are not set for armor piece in set " + setId);
            }
        }

        String registryName() {
            if (registryName == null) {
                throw new IllegalStateException("Registry name must be provided");
            }
            return registryName;
        }

        WarbornArmorItem create(ArmorItem.Type type) {
            String name = registryName();
            WarbornArmorItem item = new WarbornArmorItem(name, materialProvider.material(type), type,
                    propertiesProvider.properties(type), visuals, bones != null ? bones : ArmorBonesSpec.defaults(type),
                    attributes, dyeable);
            // Add vision capabilities for helmets
            if (type == ArmorItem.Type.HELMET) {
                for (String capability : visionCapabilities) {
                    item.addVisionCapability(capability);
                }
            }
            return item;
        }

        public interface MaterialProvider {
            Holder<ArmorMaterial> material(ArmorItem.Type type);
        }

        public interface PropertiesProvider {
            Item.Properties properties(ArmorItem.Type type);

            static PropertiesProvider identity() {
                return type -> new Item.Properties().stacksTo(1);
            }
        }
    }

    public static final class ArmorPieceBuilder {
        private final String setId;
        private final ArmorItem.Type type;
        private final int sequenceIndex;
        private final List<ArmorAttributeSpec> attributes = new ArrayList<>();
        private final List<String> visionCapabilities = new ArrayList<>();
        private ArmorPieceDefinition.MaterialProvider materialProvider;
        private ArmorVisualSpec visuals;
        private ArmorBonesSpec bones;
        private ArmorPieceDefinition.PropertiesProvider propertiesProvider;
        private String registryName;
        private boolean dyeable;

        private ArmorPieceBuilder(String setId, ArmorItem.Type type, ArmorPieceDefinition defaults, int sequenceIndex) {
            this.setId = setId;
            this.type = type;
            this.sequenceIndex = sequenceIndex;
            if (defaults != null) {
                this.materialProvider = defaults.materialProvider;
                this.visuals = defaults.visuals;
                this.bones = defaults.bones;
                this.propertiesProvider = defaults.propertiesProvider;
                this.registryName = defaults.registryName;
                this.attributes.addAll(defaults.attributes);
                this.visionCapabilities.addAll(defaults.visionCapabilities);
                this.dyeable = defaults.dyeable;
            }
        }

        public ArmorPieceBuilder registryName(String registryName) {
            this.registryName = registryName;
            return this;
        }

        public ArmorPieceBuilder material(ArmorPieceDefinition.MaterialProvider materialProvider) {
            this.materialProvider = materialProvider;
            return this;
        }

        public ArmorPieceBuilder durability() {
            return this;
        }

        public ArmorPieceBuilder dyable(boolean dyeable) {
            this.dyeable = dyeable;
            return this;
        }

        public ArmorPieceBuilder dyeable(boolean dyeable) {
            return dyable(dyeable);
        }

        public ArmorPieceBuilder visuals(Consumer<ArmorVisualSpec.Builder> consumer) {
            ArmorVisualSpec.Builder builder = ArmorVisualSpec.builder();
            consumer.accept(builder);
            this.visuals = builder.build();
            return this;
        }

        public ArmorPieceBuilder bones(UnaryOperator<ArmorBonesSpec.Builder> operator) {
            ArmorBonesSpec.Builder builder = operator.apply(ArmorBonesSpec.builder());
            this.bones = builder.build();
            return this;
        }

        public ArmorPieceBuilder properties(UnaryOperator<Item.Properties> operator) {
            this.propertiesProvider = type1 -> {
                Item.Properties base = new Item.Properties().stacksTo(1);
                return operator.apply(base);
            };
            return this;
        }

        public ArmorPieceBuilder attribute(ArmorAttributeSpec attribute) {
            this.attributes.add(attribute);
            return this;
        }

        public ArmorPieceBuilder bulletResistance(double value) {
            return bulletResistance(value, true);
        }

        public ArmorPieceBuilder bulletResistance(double value, boolean scaleWithDurability) {
            this.attributes.add(ArmorAttributeSpec.bulletResistance(value, scaleWithDurability));
            return this;
        }

        // ==================== Vision Capability Methods ====================

        /**
         * Add NVG (Night Vision Goggles) capability to this helmet
         * Only works for helmets - ignored for other armor types
         */
        public ArmorPieceBuilder withNVG() {
            if (type == ArmorItem.Type.HELMET) {
                this.visionCapabilities.add(WarbornArmorItem.TAG_NVG);
            }
            return this;
        }

        /**
         * Add Thermal Vision capability to this helmet
         * Only works for helmets - ignored for other armor types
         */
        public ArmorPieceBuilder withThermal() {
            if (type == ArmorItem.Type.HELMET) {
                this.visionCapabilities.add(WarbornArmorItem.TAG_THERMAL);
            }
            return this;
        }

        private ArmorPieceDefinition build() {
            if (materialProvider == null) {
                throw new IllegalStateException("Material is required for armor piece " + setId + " - " + type);
            }
            if (visuals == null) {
                throw new IllegalStateException("Visuals are required for armor piece " + setId + " - " + type);
            }
            ArmorPieceDefinition.PropertiesProvider properties = propertiesProvider != null ? propertiesProvider
                    : ArmorPieceDefinition.PropertiesProvider.identity();
            ArmorBonesSpec boneSpec = bones != null ? bones : ArmorBonesSpec.defaults(type);
            String registry = registryName != null ? registryName : defaultName();
            return new ArmorPieceDefinition(registry, materialProvider, visuals, boneSpec, properties, attributes,
                    visionCapabilities, dyeable);
        }

        private String defaultName() {
            String suffix = type.name().toLowerCase(Locale.ROOT);
            if (sequenceIndex <= 0) {
                return setId + "_" + suffix;
            }
            return setId + "_" + suffix + "_" + (sequenceIndex + 1);
        }
    }
}
