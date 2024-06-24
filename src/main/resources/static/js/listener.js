let checkbox = document.getElementById('checkbox');
let checkmark = document.getElementById('checkmark');
checkbox.addEventListener('click', function () {
  let pathElement = checkbox.querySelector('path');
  if (pathElement.getAttribute('fill') === 'white') {
    pathElement.setAttribute('fill', '#008BAD');
    checkmark.setAttribute('opacity', '1');
  } else {
    pathElement.setAttribute('fill', 'white');
    checkmark.setAttribute('opacity', '0');
  }
});

function confirmDelete(clientId) {
  console.log("Client ID: " + clientId)
  return confirm("Are you sure you want to delete client with ID " + clientId + "?");
}