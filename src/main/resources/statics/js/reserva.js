
document.addEventListener('DOMContentLoaded', function () {
    const cancelarModal = document.getElementById('cancelarModal');
    if (!cancelarModal) {
        return;
    }
    cancelarModal.addEventListener('show.bs.modal', function (event) {
        const boton = event.relatedTarget;
        document.getElementById('cancelarId').value = boton.getAttribute('data-id');
        document.getElementById('cancelarDescripcion').textContent = boton.getAttribute('data-descripcion');
    });
});
