<template>
  <div class="container-fluid">
    <div class="card jh-card">
      <!-- bouttons pour affiches mes medecins et les patients -->
      <h2 id="page-heading" data-cy="RendezVousHeading">
        <span id="rendez-vous-heading">Appointments</span>
      </h2>
      <!-- end bouttons pour affiches mes medecins et les patients -->

      <hr />

      <!-- Fullcalendar -->
      <FullCalendar :options="calendarOptions" />
      <!-- end Fullcalendar -->

      <!-- modal create rdv -->
      <b-modal ref="createEntity" id="createEntity">
        <span slot="modal-title"
          ><span id="pathogeneApp.rendezVous.create.question" data-cy="rendezVousCreateDialogHeading">Give the details</span></span
        >
        <div class="modal-body">
          <div class="form-group">
            <label class="form-control-label">Patient</label>
            <select class="form-control" id="rendez-vous-patient" data-cy="patient" name="patient" v-model="idPatient">
              <option v-for="patient in patients" :key="patient.id" v-bind:value="patient.id">
                {{ patient.nom }} {{ patient.prenom }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="rendez-vous-medecin">Doctor </label>
            <select
              class="form-control"
              id="rendez-vous-medecin"
              data-cy="medecin"
              name="medecin"
              v-model="idMedecin"
              @change="handleMedecinChange"
            >
              <option v-for="medecin in medecins" :key="medecin.id" v-bind:value="medecin.id">
                {{ medecin.nom }} {{ medecin.prenom }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Hour </label>
            <input list="calltimeslist" class="form-control" v-model="$v.rendezVous.heure.$model" />
          </div>
        </div>
        <div slot="modal-footer">
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-create-rendezVous"
            data-cy="entityConfirmCreateButton"
            v-on:click="save()"
          >
            Save
          </button>
        </div>
      </b-modal>
      <!-- end modal create rdv -->

      <!-- modal delete rdv -->
      <b-modal ref="removeEntity" id="removeEntity">
        <span slot="modal-title"
          ><span id="pathogeneApp.rendezVous.delete.question" data-cy="rendezVousDeleteDialogHeading">Choose operation</span></span
        >
        <div class="modal-body"></div>
        <div slot="modal-footer">
          <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Exit operation</button>
          <button
            type="button"
            class="btn btn-success"
            id="jhi-confirm-vlaide-rendezVous"
            data-cy="entityConfirmVaideButton"
            v-on:click="valideRendezVous()"
          >
            Validate
          </button>
          <button
            type="button"
            class="btn btn-info"
            id="jhi-confirm-reject-rendezVous"
            data-cy="entityConfirmRejectButton"
            v-on:click="rejectRendezVous()"
          >
            Reject
          </button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-rendezVous"
            data-cy="entityConfirmDeleteButton"
            v-on:click="removeRendezVous()"
          >
            Delete
          </button>
        </div>
      </b-modal>
      <!-- end modal delete rdv -->
    </div>

    <datalist id="calltimeslist">
      <option v-for="time in availableTimes" :key="time" :value="time" />
    </datalist>
  </div>
</template>

<script lang="ts" src="./rendez-vous.component.ts"></script>
