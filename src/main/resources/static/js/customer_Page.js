const selectedItemIds = new Set();

function toggleItemSelection(el) {
    const id = el.dataset.itemId;
    if (selectedItemIds.has(id)) {
        selectedItemIds.delete(id);
        el.classList.remove("selected");
    } else {
        selectedItemIds.add(id);
        el.classList.add("selected");
    }

    const info = document.getElementById("selectionInfo");
    if (info) {
        info.textContent = selectedItemIds.size > 0
            ? `Selected items: ${selectedItemIds.size}`
            : "";
    }
}

function onAddToYourCartClick() {
    const info = document.getElementById("selectionInfo");
    if (!info) return;

    if (selectedItemIds.size === 0) {
        info.textContent = "Please click at least one item first.";
    } else {
        info.textContent = `Selected ${selectedItemIds.size} item(s).`;
    }
}
/**
 * Updates the order summary text with the selected item count and total price.
 */
function updateOrderSummary() {
    const info = document.getElementById("selectionInfo");
    if (!info) return;

    // Collect all currently checked item checkboxes
    const checkedBoxes = document.querySelectorAll(".item-checkbox:checked");
    if (checkedBoxes.length === 0) {
        info.textContent = "";
        return;
    }

    // Sum prices from the parent menu item's data attribute
    let total = 0;
    checkedBoxes.forEach((cb) => {
        const li = cb.closest(".menu-list-item");
        if (li) {
            total += Number(li.dataset.itemPrice || 0);
        }
    });

    info.textContent = `Selected items: ${checkedBoxes.length} · Total: ${total.toFixed(2)} €`;
}

// Initialize item selection and form validation once the DOM is ready
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".menu-list-item").forEach((li) => {
        // Skip unavailable items so they cannot be selected
        if (li.dataset.available !== "true") return;

        const checkbox = li.querySelector(".item-checkbox");
        if (!checkbox) return;

        // Toggle checkbox when clicking anywhere else on the list item
        li.addEventListener("click", (event) => {
            if (event.target === checkbox) return;
            checkbox.checked = !checkbox.checked;
            li.classList.toggle("selected", checkbox.checked);
            updateOrderSummary();
        });

        // Sync visual selection state when the checkbox itself changes
        checkbox.addEventListener("change", () => {
            li.classList.toggle("selected", checkbox.checked);
            updateOrderSummary();
        });
    });

    // Prevent submitting an empty order without at least one selected item
    const orderForm = document.getElementById("orderForm");
    if (orderForm) {
        orderForm.addEventListener("submit", (event) => {
            const checkedCount = document.querySelectorAll(".item-checkbox:checked").length;
            if (checkedCount === 0) {
                event.preventDefault();
                const info = document.getElementById("selectionInfo");
                if (info) {
                    info.textContent = "Please select at least one item before placing an order.";
                }
            }
        });
    }
});
