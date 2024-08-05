function enableFieldsBasedOnDoctorSelect() {
    $(document).ready(function () {
        $("#appointmentDate").prop("disabled", true);
        $("#appointmentHour").prop("disabled", true);

        $("#doctorSelect").change(function () {
            var selectedDoctorId = $(this).val();
            if (selectedDoctorId !== "") {
                $("#appointmentDate").prop("disabled", false);
            } else {
                $("#appointmentDate").prop("disabled", true);
                $("#appointmentHour").prop("disabled", true);
            }
        });

        $("#appointmentDate").change(function () {
            var selectedDate = $(this).val();
            if (selectedDate !== "") {
                $("#appointmentHour").prop("disabled", false);
            } else {
                $("#appointmentHour").prop("disabled", true);
            }
        });
    });
}

function displayAvailableHoursBasedOnDoctorIdAndDate() {
    $(document).ready(function () {
        $("#appointmentDate").change(function () {
            var selectedDate = $(this).val();
            var selectedDoctorId = $("#doctorSelect").val();

            $.ajax({
                url: "/api/getAvailableHours",
                method: "GET",
                data: {
                    doctorId: selectedDoctorId,
                    appointmentDate: selectedDate
                },
                success: function (response) {
                    var appointmentHourSelect = $("#appointmentHour");
                    appointmentHourSelect.empty();
                    var selectAppointmentHour = $("#selectAppointmentHour").val();
                    appointmentHourSelect.append('<option value="">' + selectAppointmentHour + '</option>');

                    for (var i = 0; i < response.length; i++) {
                        appointmentHourSelect.append('<option value="' + response[i] + '">' + response[i] + '</option>');
                    }
                },
                error: function () {

                }
            });
        });
    });
}

function showLoadingOverlayIfFormIsValid() {
    const overlay = document.getElementById("loading-overlay-modal");

    if (isFormValid()) {
        overlay.style.display = "flex";
        document.body.style.overflow = "hidden";
    }
}

function isFormValid() {
    const fields = [
        'patientFullName',
        'patientEmail',
        'doctorSelect',
        'appointmentDate',
        'appointmentHour',
        'diseases'
    ];

    for (const fieldId of fields) {
        if (!isFieldNotEmpty(fieldId)) {
            return false;
        }
    }

    if (!isValidDateFromTomorrow('appointmentDate')) {
        return false;
    }

    return true;
}

function isFieldNotEmpty(fieldId) {
    const value = document.getElementById(fieldId).value;
    return value.trim() !== '';
}

function isValidDateFromTomorrow(fieldId) {
    const dateString = document.getElementById(fieldId).value;
    const inputDate = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const tomorrow = new Date(today);
    tomorrow.setDate(today.getDate() + 1);

    return inputDate >= tomorrow;
}

// function hideLoadingOverlay() {
//     const overlay = document.getElementById("loading-overlay-modal");
//     overlay.style.display = "none";
//
//     document.body.style.overflow = "auto";
// }