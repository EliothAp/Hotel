// Carga los datos del hotel en el modal Agregar o Actualizar
document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('empresaModal');
    if (!modal) {
        return;
    }
    const form = document.getElementById('empresaForm');
    const header = document.getElementById('empresaModalHeader');
    const titulo = document.getElementById('empresaModalTitulo');
    const id = document.getElementById('empresaId');
    const nombre = document.getElementById('empresaNombre');
    const correo = document.getElementById('empresaCorreo');
    const telefono = document.getElementById('empresaTelefono');
    const activo = document.getElementById('empresaActivo');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const modo = button.getAttribute('data-modo');
        form.reset();
        id.value = '';
        activo.checked = true;
        titulo.textContent = button.getAttribute('data-titulo') || '';
        header.classList.remove('bg-success', 'bg-warning', 'text-white');
        if (modo === 'editar') {
            id.value = button.getAttribute('data-id') || '';
            nombre.value = button.getAttribute('data-nombre') || '';
            correo.value = button.getAttribute('data-correo') || '';
            telefono.value = button.getAttribute('data-telefono') || '';
            activo.checked = button.getAttribute('data-activo') === 'true';
            header.classList.add('bg-warning');
        } else {
            header.classList.add('bg-success', 'text-white');
        }
    });
});

// Pasa el id y el nombre del hotel al modal de eliminar
document.addEventListener('DOMContentLoaded', function () {
    const confirmModal = document.getElementById('confirmModal');
    if (!confirmModal) {
        return;
    }
    confirmModal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        document.getElementById('modalId').value = button.getAttribute('data-bs-id');
        document.getElementById('modalDescripcion').textContent = button.getAttribute('data-bs-descripcion');
    });
});
