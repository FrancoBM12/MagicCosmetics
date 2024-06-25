package com.francobm.magicosmetics.utils;

import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class DefaultAttributes {

    public static Multimap<Attribute, AttributeModifier> defaultsOf(org.bukkit.inventory.ItemStack item) {
        Multimap<Attribute, AttributeModifier> result = ArrayListMultimap.create();
        if(item == null)
            return result;
        Material mat = item.getType();
        double armor = getDefaultArmor(mat);
        double tough = getDefaultArmorToughness(mat);
        double knockBack = getKnockBackResistance(mat);
        double damage = getDefaultAttackDamage(mat);
        double speed = getDefaultAttackSpeed(mat);
        if (armor > 0) {
            result.put(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), Attribute.GENERIC_ARMOR.getKey().getKey(), armor, AttributeModifier.Operation.ADD_NUMBER, guessEquipmentSlotOf(mat)));
        }
        if(knockBack > 0){
            result.put(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), Attribute.GENERIC_KNOCKBACK_RESISTANCE.getKey().getKey(), knockBack, AttributeModifier.Operation.ADD_NUMBER, guessEquipmentSlotOf(mat)));
        }
        if (tough > 0) {
            result.put(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), Attribute.GENERIC_ARMOR_TOUGHNESS.getKey().getKey(), tough, AttributeModifier.Operation.ADD_NUMBER, guessEquipmentSlotOf(mat)));
        }
        if (damage > 0) {
            result.put(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), Attribute.GENERIC_ATTACK_DAMAGE.getKey().getKey(), damage, AttributeModifier.Operation.ADD_NUMBER, guessEquipmentSlotOf(mat)));
        }
        if (speed > 0) {
            result.put(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), Attribute.GENERIC_ATTACK_SPEED.getKey().getKey(), speed, AttributeModifier.Operation.ADD_NUMBER, guessEquipmentSlotOf(mat)));
        }
        return result;
    }

    public static EquipmentSlot guessEquipmentSlotOf(Material material) {
        String itemName = material.name();
        if (itemName.contains("_HELMET")) {
            return EquipmentSlot.HEAD;
        } else if (itemName.contains("_CHESTPLATE")) {
            return EquipmentSlot.CHEST;
        } else if (itemName.contains("_LEGGINGS")) {
            return EquipmentSlot.LEGS;
        } else if (itemName.contains("_BOOTS")) {
            return EquipmentSlot.FEET;
        } else {
            return EquipmentSlot.HAND;
        }
    }

    public static double getDefaultArmor(Material mat) {
        switch (mat) {
            case LEATHER_HELMET: return 1;
            case LEATHER_CHESTPLATE: return 3;
            case LEATHER_LEGGINGS: return 2;
            case LEATHER_BOOTS: return 1;
            case GOLDEN_HELMET: return 2;
            case GOLDEN_CHESTPLATE: return 5;
            case GOLDEN_LEGGINGS: return 3;
            case GOLDEN_BOOTS: return 1;
            case CHAINMAIL_HELMET: return 2;
            case CHAINMAIL_CHESTPLATE: return 5;
            case CHAINMAIL_LEGGINGS: return 4;
            case CHAINMAIL_BOOTS: return 1;
            case IRON_HELMET: return 2;
            case IRON_CHESTPLATE: return 6;
            case IRON_LEGGINGS: return 5;
            case IRON_BOOTS: return 2;
            case DIAMOND_HELMET: return 3;
            case DIAMOND_CHESTPLATE: return 8;
            case DIAMOND_LEGGINGS: return 6;
            case DIAMOND_BOOTS: return 3;
            case NETHERITE_HELMET: return 3;
            case NETHERITE_CHESTPLATE: return 8;
            case NETHERITE_LEGGINGS: return 6;
            case NETHERITE_BOOTS: return 3;
            case TURTLE_HELMET: return 2;
            default: return 0;
        }
    }

    public static double getDefaultArmorToughness(Material mat) {
        switch (mat) {
            case DIAMOND_HELMET:
            case DIAMOND_CHESTPLATE:
            case DIAMOND_LEGGINGS:
            case DIAMOND_BOOTS:
                return 2;
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return 3;
            default: return 0;
        }
    }

    public static double getKnockBackResistance(Material mat) {
        switch (mat) {
            case NETHERITE_HELMET:
            case NETHERITE_CHESTPLATE:
            case NETHERITE_LEGGINGS:
            case NETHERITE_BOOTS:
                return 1;
            default: return 0;
        }
    }

    public static double getDefaultAttackDamage(Material mat) {
        switch (mat) {
            case WOODEN_SWORD: return 4;
            case GOLDEN_SWORD: return 4;
            case STONE_SWORD: return 5;
            case IRON_SWORD: return 6;
            case DIAMOND_SWORD: return 7;
            case NETHERITE_SWORD: return 8;
            // Axes
            case WOODEN_AXE: return 7;
            case GOLDEN_AXE: return 7;
            case STONE_AXE: return 9;
            case IRON_AXE: return 9;
            case DIAMOND_AXE: return 9;
            // Pickaxes
            case WOODEN_PICKAXE: return 2;
            case GOLDEN_PICKAXE: return 2;
            case STONE_PICKAXE: return 3;
            case IRON_PICKAXE: return 4;
            case DIAMOND_PICKAXE: return 5;
            // Shovel
            case WOODEN_SHOVEL: return 2.5;
            case GOLDEN_SHOVEL: return 2.5;
            case STONE_SHOVEL: return 3.5;
            case IRON_SHOVEL: return 4.5;
            case DIAMOND_SHOVEL: return 5.5;
            // Hoe
            case WOODEN_HOE: return 1;
            case GOLDEN_HOE: return 1;
            case STONE_HOE: return 1;
            case IRON_HOE: return 1;
            case DIAMOND_HOE: return 1;
            default: return 0;
        }
    }

    public static double getDefaultAttackSpeed(Material mat) {
        switch (mat) {
            case WOODEN_SWORD:
            case GOLDEN_SWORD:
            case STONE_SWORD:
            case IRON_SWORD:
            case DIAMOND_SWORD:
                return 1.6;
            // Axes
            case WOODEN_AXE: return 0.8;
            case GOLDEN_AXE: return 1.0;
            case STONE_AXE: return 0.8;
            case IRON_AXE: return 0.9;
            case DIAMOND_AXE: return 1.0;
            // Pickaxes
            case WOODEN_PICKAXE: return 1.2;
            case STONE_PICKAXE: return 1.2;
            case GOLDEN_PICKAXE: return 1.2;
            case IRON_PICKAXE: return 1.2;
            case DIAMOND_PICKAXE: return 1.2;
            // Shovels
            case WOODEN_SHOVEL: return 1.0;
            case GOLDEN_SHOVEL: return 1.0;
            case STONE_SHOVEL: return 1.0;
            case IRON_SHOVEL: return 1.0;
            case DIAMOND_SHOVEL: return 1.0;
            // Hoes
            case WOODEN_HOE: return 1.0;
            case GOLDEN_HOE: return 1.0;
            case STONE_HOE: return 2.0;
            case IRON_HOE: return 3.0;
            case DIAMOND_HOE: return 4.0;
            default: return 0;
        }
    }
}
