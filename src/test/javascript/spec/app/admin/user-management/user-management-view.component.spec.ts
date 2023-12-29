import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import axios from 'axios';
import sinon from 'sinon';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

import * as config from '@/shared/config/config';
import UserManagementView from '@/admin/user-management/user-management-view.vue';
import UserManagementViewClass from '@/admin/user-management/user-management-view.component';
import UserManagementService from '@/admin/user-management/user-management.service';
import { Authority } from '@/shared/security/authority';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', FontAwesomeIcon);
localVue.component('b-badge', {});
localVue.component('router-link', {});

const axiosStub = {
  get: sinon.stub(axios, 'get'),
};

describe('UserManagementView Component', () => {
  let wrapper: Wrapper<UserManagementViewClass>;
  let userManagementView: UserManagementViewClass;

  beforeEach(() => {
    wrapper = shallowMount<UserManagementViewClass>(UserManagementView, {
      store,
      i18n,
      localVue,
      provide: { userManagementService: () => new UserManagementService(), alertService: () => new AlertService() },
    });
    userManagementView = wrapper.vm;
  });

  describe('OnInit', () => {
    it('Should call load all on init', async () => {
      // GIVEN
      const userData = {
        id: 1,
        login: 'user',
        firstName: 'first',
        lastName: 'last',
        email: 'first@last.com',
        activated: true,
        langKey: 'en',
        authorities: [Authority.USER],
        createdBy: 'admin',
        createdDate: null,
        lastModifiedBy: null,
        lastModifiedDate: null,
        password: null,
      };
      axiosStub.get.resolves({ data: userData });

      // WHEN
      userManagementView.init(123);
      await userManagementView.$nextTick();

      // THEN
    });
  });
});
