package io.ejekta.kambrik.gui.screen

import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

abstract class KambrikContainerMenu<S : AbstractContainerMenu, CON : Container>(type: MenuType<S>?, syncId: Int) : AbstractContainerMenu(type, syncId) {

    abstract var container: CON

    protected fun <I : Inventory, S : Slot> makeSlotGrid(
        inventory: I,
        cols: Int,
        rows: Int,
        offX: Int = 0,
        offY: Int = 0,
        padding: Int = 0,
        startIndex: Int = 0,
        slotMaker: ( (inv: I, index: Int, x: Int, y: Int) -> S )? = null
    ) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val calcIndex = col + row * cols + startIndex
                val calcX = col * (18 + padding) + offX
                val calcY = row * (18 + padding) + offY
                if (slotMaker != null) {
                    addSlot(slotMaker(inventory, calcIndex, calcX, calcY))
                } else {
                    addSlot(Slot(inventory, calcIndex, calcX, calcY))
                }
            }
        }
    }

    override fun quickMoveStack(pPlayer: Player, invSlot: Int): ItemStack {
        var newStack = ItemStack.EMPTY
        val slot: Slot? = slots[invSlot]
        if (slot != null && slot.hasItem()) {
            val originalStack: ItemStack = slot.item
            newStack = originalStack.copy()
            if (invSlot < container.containerSize) {
                if (!moveItemStackTo(originalStack, container.containerSize, slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!moveItemStackTo(originalStack, 0, container.containerSize, false)) {
                return ItemStack.EMPTY
            }
            if (originalStack.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }
        return newStack
    }

    protected fun makePlayerInventoryGrid(playerInventory: Inventory, offX: Int, offY: Int) {
        makeSlotGrid<Inventory, Slot>(playerInventory, 9, 3, offX, offY, startIndex = 9)
    }

    protected fun makePlayerHotbarGrid(playerInventory: Inventory, offX: Int, offY: Int) {
        makeSlotGrid<Inventory, Slot>(playerInventory, 9, 1, offX, offY)
    }

    protected fun makePlayerDefaultGrid(playerInventory: Inventory, offX: Int, offY: Int) {
        makePlayerInventoryGrid(playerInventory, offX, offY)
        makePlayerHotbarGrid(playerInventory, offX, offY + 58)
    }

}