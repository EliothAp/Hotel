// Carga los datos de la habitacion en el modal Agregar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal =document.getElementById('habitacionModal');
    if (!modal) {
        return;
    }
    const form =document.getElementById('habitacionForm');
    const header =document.getElementById('habitacionModalHeader');
    const titulo =document.getElementById('habitacionModalTitulo');
    const id =document.getElementById('habitacionId');
    const tipoHabitacion =document.getElementById('habitacionIdTipoHabitacion');
    const numero =document.getElementById('habitacionNumero');
    const estado =document.getElementById('habitacionEstado');
    const rutaImagen =document.getElementById('habitacionRutaImagen');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        const modo =button.getAttribute('data-modo');
        // Limpia información de una apertura anterior
        form.reset();
        id.value = '';
        rutaImagen.value = '';
        titulo.textContent =button.getAttribute('data-titulo') || '';
        header.classList.remove('bg-info','bg-warning','text-white');
        if (modo === 'editar') {
            id.value =button.getAttribute('data-id') || '';
            tipoHabitacion.value =button.getAttribute('data-tipo-habitacion-id') || '';
            numero.value =button.getAttribute('data-numero') || '';
            estado.value =button.getAttribute('data-estado') || '';
            rutaImagen.value =button.getAttribute('data-imagen') || '';
            header.classList.add('bg-warning');
        } else {
            // Selecciona el primer tipo de habitacion disponible
            if (tipoHabitacion.options.length > 0) {
                tipoHabitacion.selectedIndex = 0;
            }
            header.classList.add('bg-info','text-white');
        }
    });
});
