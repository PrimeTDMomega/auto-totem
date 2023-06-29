package me.prime.autototem;

import net.minecraftforge.client.event.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod("autototem")
public class AutoTotemMod {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Register your event handler here
    }

    @SubscribeEvent
    public static void onContainerChanged(PlayerContainerEvent event) {
        if (!event.getPlayer().world.isRemote()) { // Only run on the server side
            if (event.getContainer().windowId == 0) { // Player's inventory window ID is 0
                // Check if there is a Totem of Undying in the player's inventory
                if (containsTotem(event.getContainer().inventorySlots)) {
                    // Check if the player's offhand is empty or holding a non-totem item
                    if (isEmptyOffhand(event.getContainer().inventorySlots) || !isHoldingTotemOffhand(event.getContainer().inventorySlots)) {
                        // Move a Totem of Undying to the player's offhand
                        moveTotemToOffhand(event.getContainer().inventorySlots);
                    }
                }
            }
        }
    }

    private static boolean containsTotem(Iterable<PlayerContainerEvent> inventorySlots) {
        for (PlayerContainerEvent slot : inventorySlots) {
            if (slot.getItemStack().getItem() instanceof net.minecraft.item.TotemItem) {
                return true;
            }
        }
        return false;
    }

    private static boolean isEmptyOffhand(Iterable<PlayerContainerEvent> inventorySlots) {
        PlayerContainerEvent offhandSlot = inventorySlots.get(45); // Offhand slot index is 45
        return offhandSlot.getItemStack().isEmpty();
    }

    private static boolean isHoldingTotemOffhand(Iterable<PlayerContainerEvent> inventorySlots) {
        PlayerContainerEvent offhandSlot = inventorySlots.get(45); // Offhand slot index is 45
        return offhandSlot.getItemStack().getItem() instanceof net.minecraft.item.TotemItem;
    }

    private static void moveTotemToOffhand(Iterable<PlayerContainerEvent> inventorySlots) {
        for (PlayerContainerEvent slot : inventorySlots) {
            if (slot.getItemStack().getItem() instanceof net.minecraft.item.TotemItem) {
                // Move the Totem of Undying to the player's offhand
                swapSlots(slot, inventorySlots.get(45)); // Offhand slot index is 45
                break;
            }
        }
    }

    private static void swapSlots(PlayerContainerEvent slot1, PlayerContainerEvent slot2) {
        net.minecraft.item.ItemStack temp = slot1.getItemStack().copy();
        slot1.getItemStack().setCount(slot2.getItemStack().getCount());
        slot1.getItemStack().setTag(slot2.getItemStack().getTag());
        slot1.getItemStack().setDamage(slot2.getItemStack().getDamage());
        slot2.getItemStack().setCount(temp.getCount());
        slot2.getItemStack().setTag(temp.getTag());
        slot2.getItemStack().setDamage(temp.getDamage());
    }