function createWordButton(word) {
    var container = document.getElementById('keywordPanel');
    var btn = document.createElement('button');
    btn.textContent = word;
    btn.className = 'word-btn';
    btn.onclick = function() { // Allow removing the word by clicking the button
        container.removeChild(btn);
        keywordList = keywordList.filter(k => k !== word); // Remove from keyword list

        // Hide the clear button if no more keywords exist
        if (keywordList.length === 0) {
            document.getElementById('clearKeywords').style.display = 'none';
        }
    };
    container.appendChild(btn);
}

// Clear all keywords
document.getElementById('clearKeywords').addEventListener('click', function() {
    document.getElementById('keywordPanel').innerHTML = '';
    keywordList = []; // Reset keyword list
    this.style.display = 'none'; // Hide the clear button
});



let keywordList = []; // Global list to store keywords

document.getElementById('filterInput').addEventListener('keypress', function(event) {
    if (event.key === "Enter") {
        event.preventDefault(); // Prevent form submission
        var input = event.target;
        var inputValue = input.value.trim();

        if (inputValue && !keywordList.includes(inputValue)) { // Avoid duplicates
            keywordList.push(inputValue); // Add to the list
            createWordButton(inputValue);
            input.value = ''; // Clear input after entering
            document.getElementById('clearKeywords').style.display = 'inline-block'; // Show clear button
        }
    }
});
