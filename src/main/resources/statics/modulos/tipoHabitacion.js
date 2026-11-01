// Carga los datos del tipo de habitacion en el modal Agregar o Editar
document.addEventListener('DOMContentLoaded', function () {
    const modal =document.getElementById('tipoHabitacionModal');
    if (!modal) {
        return;
    }
    const form =document.getElementById('tipoHabitacionForm');
    const header =document.getElementById('tipoHabitacionModalHeader');
    const titulo =document.getElementById('tipoHabitacionModalTitulo');
    const id =document.getElementById('tipoHabitacionId');
    const nombre =document.getElementById('tipoHabitacionNombre');
    const capacidad =document.getElementById('tipoHabitacionCapacidad');
    const precioNoche =document.getElementById('tipoHabitacionPrecioNoche');

    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        if (!button) {
            return;
        }
        const modo =button.getAttribute('data-modo');
        // Limpia información de una apertura anterior
        form.reset();
        id.value = '';
        titulo.textContent =button.getAttribute('data-titulo') || '';
        header.classList.remove('bg-hotel','bg-hotel-claro','text-white');
        if (modo === 'editar') {
            id.value =button.getAttribute('data-id') || '';
            nombre.value =button.getAttribute('data-nombre') || '';
            capacidad.value =button.getAttribute('data-capacidad') || '';
            precioNoche.value =button.getAttribute('data-precio-noche') || '';
            header.classList.add('bg-hotel-claro');
        } else {
            header.classList.add('bg-hotel','text-white');
        }
    });
});
