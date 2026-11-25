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