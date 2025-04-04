package com.songoda.skyblock.utils.world.entity;

import com.songoda.core.compatibility.CompatibleMaterial;
import com.songoda.core.compatibility.MajorServerVersion;
import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.nms.Nms;
import com.songoda.skyblock.utils.item.ItemStackUtil;
import org.bukkit.Art;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Horse;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Painting;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Pig;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public final class EntityUtil {
    public static EntityData convertEntityToEntityData(Entity entity, int x, int y, int z) {
        return new EntityData(Nms.getImplementations().getNbt().of(entity).serialize("Attributes"), x, y, z);
    }

    public static void convertEntityDataToEntity(EntityData entityData, Location loc) {
        Entity entity = loc.getWorld().spawnEntity(loc, EntityType.valueOf(entityData.getEntityType().toUpperCase()));
        entity.setCustomName(entityData.getCustomName());
        entity.setCustomNameVisible(entityData.isCustomNameVisible());
        entity.setFireTicks(entityData.getFireTicks());
        entity.setTicksLived(entityData.getTicksLived());

        if (entity instanceof ArmorStand) {
            ArmorStand armorStand = (ArmorStand) entity;
            armorStand.setArms(entityData.hasArms());

            if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
                armorStand.setItemInHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
            }

            if (entityData.getHelmet() != null && !entityData.getHelmet().isEmpty()) {
                armorStand.setHelmet(ItemStackUtil.deserializeItemStack(entityData.getHelmet()));
            }

            if (entityData.getChestplate() != null && !entityData.getChestplate().isEmpty()) {
                armorStand.setChestplate(ItemStackUtil.deserializeItemStack(entityData.getChestplate()));
            }

            if (entityData.getLeggings() != null && !entityData.getLeggings().isEmpty()) {
                armorStand.setLeggings(ItemStackUtil.deserializeItemStack(entityData.getLeggings()));
            }

            if (entityData.getBoots() != null && !entityData.getBoots().isEmpty()) {
                armorStand.setBoots(ItemStackUtil.deserializeItemStack(entityData.getBoots()));
            }

            armorStand.setBasePlate(entityData.hasBasePlate());
            armorStand.setVisible(entityData.isVisible());
            armorStand.setSmall(entityData.isSmall());
            armorStand.setMarker(entityData.isMarker());

            String[] bodyPose = entityData.getBodyPose().split(" ");
            armorStand.setBodyPose(new EulerAngle(Double.parseDouble(bodyPose[0]), Double.parseDouble(bodyPose[1]),
                    Double.parseDouble(bodyPose[2])));

            String[] headPose = entityData.getHeadPose().split(" ");
            armorStand.setHeadPose(new EulerAngle(Double.parseDouble(headPose[0]), Double.parseDouble(headPose[1]),
                    Double.parseDouble(headPose[2])));

            String[] leftArmPose = entityData.getLeftArmPose().split(" ");
            armorStand.setLeftArmPose(new EulerAngle(Double.parseDouble(leftArmPose[0]),
                    Double.parseDouble(leftArmPose[1]), Double.parseDouble(leftArmPose[2])));

            String[] leftLegPose = entityData.getLeftLegPose().split(" ");
            armorStand.setLeftLegPose(new EulerAngle(Double.parseDouble(leftLegPose[0]),
                    Double.parseDouble(leftLegPose[1]), Double.parseDouble(leftLegPose[2])));

            String[] rightArmPose = entityData.getRightArmPose().split(" ");
            armorStand.setRightArmPose(new EulerAngle(Double.parseDouble(rightArmPose[0]),
                    Double.parseDouble(rightArmPose[1]), Double.parseDouble(rightArmPose[2])));

            String[] rightLegPose = entityData.getRightLegPose().split(" ");
            armorStand.setRightLegPose(new EulerAngle(Double.parseDouble(rightLegPose[0]),
                    Double.parseDouble(rightLegPose[1]), Double.parseDouble(rightLegPose[2])));
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            EntityEquipment entityEquipment = livingEntity.getEquipment();

            if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_9)) {
                if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_10)) {
                    livingEntity.setAI(entityData.hasAI());
                }

                if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
                    entityEquipment.setItemInMainHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
                }

                if (entityData.getOffHand() != null && !entityData.getOffHand().isEmpty()) {
                    entityEquipment.setItemInOffHand(ItemStackUtil.deserializeItemStack(entityData.getOffHand()));
                }

                entityEquipment.setItemInMainHandDropChance(entityData.getHandChance());
                entityEquipment.setItemInOffHandDropChance(entityData.getOffHandChance());
            } else {
                if (entityData.getHand() != null && !entityData.getHand().isEmpty()) {
                    entityEquipment.setItemInHand(ItemStackUtil.deserializeItemStack(entityData.getHand()));
                }

                entityEquipment.setItemInHandDropChance(entityData.getHandChance());
            }

            if (entityData.getHelmet() != null && !entityData.getHelmet().isEmpty()) {
                entityEquipment.setHelmet(ItemStackUtil.deserializeItemStack(entityData.getHelmet()));
            }

            if (entityData.getChestplate() != null && !entityData.getChestplate().isEmpty()) {
                entityEquipment.setChestplate(ItemStackUtil.deserializeItemStack(entityData.getChestplate()));
            }

            if (entityData.getLeggings() != null && !entityData.getLeggings().isEmpty()) {
                entityEquipment.setLeggings(ItemStackUtil.deserializeItemStack(entityData.getLeggings()));
            }

            if (entityData.getBoots() != null && !entityData.getBoots().isEmpty()) {
                entityEquipment.setBoots(ItemStackUtil.deserializeItemStack(entityData.getBoots()));
            }

            entityEquipment.setHelmetDropChance(entityData.getHelmetChance());
            entityEquipment.setChestplateDropChance(entityData.getChestplateChance());
            entityEquipment.setLeggingsDropChance(entityData.getLeggingsChance());
            entityEquipment.setBootsDropChance(entityData.getBootsChance());

            if (entity instanceof Bat) {
                ((Bat) entity).setAwake(entityData.isAwake());
            } else if (entity instanceof Creeper) {
                ((Creeper) entity).setPowered(entityData.isPowered());
            } else if (entity instanceof Enderman) {
                if (entityData.getCarryBlock() != null && !entityData.getCarryBlock().isEmpty()) {
                    String[] materialData = entityData.getCarryBlock().split(":");

                    byte data = Byte.parseByte(materialData[1]);
                    Material material = CompatibleMaterial.getMaterial(materialData[0].toUpperCase()).get().parseMaterial();

                    if (material != null) {
                        if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_13)) {
                            ((Enderman) entity).setCarriedBlock(Bukkit.getServer().createBlockData(material));
                        } else {
                            ((Enderman) entity).setCarriedMaterial(new MaterialData(material, data));
                        }
                    }
                }
            } else if (entity instanceof Horse) {
                Horse horse = ((Horse) entity);
                horse.setColor(Horse.Color.valueOf(entityData.getHorseColor().toUpperCase()));
                horse.setStyle(Horse.Style.valueOf(entityData.getHorseStyle().toUpperCase()));

                List<ItemStack> items = new ArrayList<>();

                for (String inventoryList : entityData.getInventory()) {
                    items.add(ItemStackUtil.deserializeItemStack(inventoryList));
                }

                horse.getInventory().setContents(items.toArray(new ItemStack[0]));
            } else if (entity instanceof IronGolem) {
                ((IronGolem) entity).setPlayerCreated(entityData.isCreatedByPlayer());
            } else if (entity instanceof Ocelot) {
                ((Ocelot) entity).setCatType(Ocelot.Type.valueOf(entityData.getOcelotType().toUpperCase()));
            } else if (entity instanceof Pig) {
                ((Pig) entity).setSaddle(entityData.hasSaddle());
            } else if (entity instanceof Zombie) {
                ((Zombie) entity).setBaby(entityData.isBaby());
            } else if (entity instanceof PigZombie) {
                PigZombie pigZombie = ((PigZombie) entity);
                pigZombie.setAngry(entityData.isAngry());
                pigZombie.setAnger(entityData.getAngerLevel());
            } else if (entity instanceof Rabbit) {
                ((Rabbit) entity).setRabbitType(Rabbit.Type.valueOf(entityData.getRabbitType().toUpperCase()));
            } else if (entity instanceof Sheep) {
                Sheep sheep = ((Sheep) entity);
                sheep.setSheared(entityData.isSheared());
                sheep.setColor(DyeColor.valueOf(entityData.getColor().toUpperCase()));
            } else if (entity instanceof Slime) {
                ((Slime) entity).setSize(entityData.getSlimeSize());
            } else if (entity instanceof Snowman) {
                ((Snowman) entity).setDerp(entityData.isDerp());
            } else if (entity instanceof Villager) {
                Villager villager = ((Villager) entity);
                villager.setProfession(Villager.Profession.valueOf(entityData.getProfession().toUpperCase()));

                List<ItemStack> items = new ArrayList<>();

                for (String inventoryList : entityData.getInventory()) {
                    items.add(ItemStackUtil.deserializeItemStack(inventoryList));
                }

                villager.getInventory().setContents(items.toArray(new ItemStack[0]));
            }

            if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_11)) {
                if (entity instanceof Llama) {
                    Llama llama = ((Llama) entity);
                    llama.setColor(Llama.Color.valueOf(entityData.getLlamaColor().toUpperCase()));
                    llama.setStrength(entityData.getLlamaStrength());

                    List<ItemStack> items = new ArrayList<>();

                    for (String inventoryList : entityData.getInventory()) {
                        items.add(ItemStackUtil.deserializeItemStack(inventoryList));
                    }

                    llama.getInventory().setContents(items.toArray(new ItemStack[0]));
                }

                if (MajorServerVersion.isServerVersionAtLeast(MajorServerVersion.V1_11)) {
                    if (entity instanceof Parrot) {
                        ((Parrot) entity)
                                .setVariant(Parrot.Variant.valueOf(entityData.getParrotVariant().toUpperCase()));
                    }
                }
            }
        }

        if (entity instanceof Ageable) {
            Ageable ageable = ((Ageable) entity);
            ageable.setBreed(entityData.canBreed());
            ageable.setAge(entityData.getAge());
            ageable.setAgeLock(entityData.isAgeLock());

            if (!entityData.isBaby()) {
                ageable.setAdult();
            }
        } else if (entity instanceof Vehicle) {
            if (entity instanceof Boat) {
                ((Boat) entity).setWoodType(TreeSpecies.valueOf(entityData.getWoodType().toUpperCase()));
            } else if (entity instanceof StorageMinecart || entity instanceof HopperMinecart) {

                List<ItemStack> items = new ArrayList<>();

                for (String inventoryList : entityData.getInventory()) {
                    items.add(ItemStackUtil.deserializeItemStack(inventoryList));
                }

                ((InventoryHolder) entity).getInventory().setContents(items.toArray(new ItemStack[0]));
            }
        } else if (entity instanceof Hanging) {
            if (entity instanceof ItemFrame) {
                ItemFrame itemFrame = ((ItemFrame) entity);

                if (!entityData.getItem().isEmpty()) {
                    itemFrame.setItem(ItemStackUtil.deserializeItemStack(entityData.getItem()));
                }

                itemFrame.setRotation(Rotation.valueOf(entityData.getRotate().toUpperCase()));
            } else if (entity instanceof Painting) {
                ((Painting) entity).setArt(Art.valueOf(entityData.getArt()));
            }
        }
    }
}
