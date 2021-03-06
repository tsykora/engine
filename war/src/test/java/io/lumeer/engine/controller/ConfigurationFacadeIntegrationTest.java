/*
 * -----------------------------------------------------------------------\
 * Lumeer
 *  
 * Copyright (C) 2016 - 2017 the original author or authors.
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -----------------------------------------------------------------------/
 */
package io.lumeer.engine.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.lumeer.engine.IntegrationTestBase;
import io.lumeer.engine.api.data.DataDocument;
import io.lumeer.engine.api.data.DataStorage;
import io.lumeer.engine.controller.configuration.ConfigurationManipulator;
import io.lumeer.engine.controller.configuration.ConfigurationManipulatorIntegrationTest;
import io.lumeer.engine.provider.DataStorageProvider;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Optional;
import javax.inject.Inject;

/**
 * @author <a href="mailto:mat.per.vt@gmail.com">Matej Perejda</a>
 */
@RunWith(Arquillian.class)
public class ConfigurationFacadeIntegrationTest extends IntegrationTestBase {

   private final String PORT_KEY = "db_port_test";
   private final String DBHOST_KEY = "db_host_test";
   private final String DBURL_KEY = "db_url_test";
   private final String CONFIG_DOCUMENT_KEY = "config";
   private final String DOCUMENT_PROPERTY_KEY = "document_property";

   private final String DUMMY_EMAIL_PREFIX = "pepa";
   private final String DUMMY_EMAIL_DOMAIN = "@zdepa.cz";
   private final String DUMMY_DBHOST_VALUE = "lumeer";
   private final String DUMMY_DBURL_VALUE = "mongodb://" + DUMMY_DBHOST_VALUE;
   private final String DUMMY_VALUE = "dummyValue";

   private final String DEFAULT_DBHOST_VALUE = "localhost";
   private final int DEFAULT_PORT_VALUE = 27017;
   private final String DEFAULT_NOT_EXISTED_KEY = "not_existed_key";

   private final int DUMMY_PORT_VALUE = 63667;
   private final int BEFORE_SIZE_RESET = 4;
   private final int AFTER_SIZE_RESET = 0;

   private DataStorage systemDataStorage;

   @Inject
   private ConfigurationFacade configurationFacade;

   @Inject
   private ConfigurationManipulator configurationManipulator;

   @Inject
   private UserFacade userFacade;

   @Inject
   private OrganisationFacade organisationFacade;

   @Inject
   private ProjectFacade projectFacade;

   @Inject
   private DataStorageProvider dataStorageProvider;

   @Before
   public void setUp() throws Exception {
      systemDataStorage = dataStorageProvider.getSystemStorage();

      projectFacade.setCurrentProjectId("configProject");
      organisationFacade.setOrganisationId("configOrg");

      if (isDatabaseCollection(ConfigurationFacade.USER_CONFIG_COLLECTION)) {
         systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);
      }
      if (isDatabaseCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION)) {
         systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);
      }
      if (isDatabaseCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION)) {
         systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);
      }
   }

   @Test
   public void testGetConfigurationString() throws Exception {
      // #1 if both system collections are empty, default value will be returned
      final Optional defaultValue = configurationFacade.getConfigurationString(DBHOST_KEY);
      assertThat(defaultValue.get()).isEqualTo(DEFAULT_DBHOST_VALUE);

      systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #2 if the org. system collection has key-value
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.ORGANISATION);
      assertThat(configurationFacade.getConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);

      systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #3 if the project system collection has key-value
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.PROJECT);
      assertThat(configurationFacade.getConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);

      systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #4 if the user system collection has key-value
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      assertThat(configurationFacade.getConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);

      // #5 if none of system values exists in collections, neither the default value
      assertThat(configurationFacade.getConfigurationString(DEFAULT_NOT_EXISTED_KEY)).isEqualTo(Optional.empty());
   }

   @Test
   public void testGetConfigurationInteger() throws Exception {
      // #1 if both system collections are empty, default value will be returned
      final Optional defaultValue = configurationFacade.getConfigurationInteger(PORT_KEY);
      assertThat(defaultValue.get()).isEqualTo(DEFAULT_PORT_VALUE);

      systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);
      systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #2 if the user system collection has key-value
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      assertThat(configurationFacade.getConfigurationInteger(PORT_KEY).get()).isEqualTo(DUMMY_PORT_VALUE);

      // #3 string key is present but cannot be obtain as integer
      assertThat(configurationFacade.getConfigurationString(DBHOST_KEY)).isNotEmpty();
      assertThat(configurationFacade.getConfigurationInteger(DBHOST_KEY)).isEmpty();

      // #3 if none of system values exists in collections, neither the default value
      assertThat(configurationFacade.getConfigurationString(DEFAULT_NOT_EXISTED_KEY)).isEqualTo(Optional.empty());
   }

   @Test
   public void testGetConfigurationDocument() throws Exception {
      // #1 if the user system collection has key-value
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      assertThat(configurationFacade.getConfigurationDocument(DOCUMENT_PROPERTY_KEY).get()).isEqualTo(ConfigurationManipulatorIntegrationTest.createDummyDataDocument());

      // #3 if none of system values exists in collections, neither the default value
      assertThat(configurationFacade.getConfigurationDocument(DEFAULT_NOT_EXISTED_KEY)).isEqualTo(Optional.empty());
   }

   @Test
   public void testSetAndGetUserConfigurationString() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);

      // #1 if the default value exists
      configurationFacade.setUserConfigurationString(DBHOST_KEY, DUMMY_DBHOST_VALUE);
      assertThat(configurationFacade.getUserConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);
      systemDataStorage.dropCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);

      // #2 if the system user collection is filled and key exists
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      configurationFacade.setUserConfigurationString(DBURL_KEY, DUMMY_VALUE);
      assertThat(configurationFacade.getUserConfigurationString(DBURL_KEY).get()).isEqualTo(DUMMY_VALUE);

      // #3 if none of system values exists in collections, neither the default value
      assertThat(configurationFacade.getConfigurationString(DEFAULT_NOT_EXISTED_KEY)).isEqualTo(Optional.empty());
   }

   @Test
   public void testSetAndGetUserConfigurationInteger() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);

      // #1 if the default value exists
      configurationFacade.setUserConfigurationInteger(PORT_KEY, DUMMY_PORT_VALUE);
      assertThat(configurationFacade.getUserConfigurationInteger(PORT_KEY).get()).isEqualTo(DUMMY_PORT_VALUE);

      // #2 if none of system values exists in collections, neither the default value
      assertThat(configurationFacade.getConfigurationInteger(DEFAULT_NOT_EXISTED_KEY)).isEqualTo(Optional.empty());
   }

   @Test
   public void testSetAndGetUserConfigurationDocument() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.USER_CONFIG_COLLECTION);

      final DataDocument dummyDocument = ConfigurationManipulatorIntegrationTest.createDummyDataDocument();
      configurationFacade.setUserConfigurationDocument(DOCUMENT_PROPERTY_KEY, dummyDocument);
      final DataDocument document = configurationFacade.getUserConfigurationDocument(DOCUMENT_PROPERTY_KEY).get();

      assertThat(dummyDocument).isEqualTo(document);
   }

   @Test
   public void testSetAndGetProjectConfigurationString() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);

      // #1 if the system user collection is empty
      configurationFacade.setProjectConfigurationString(DBHOST_KEY, DUMMY_DBHOST_VALUE);
      assertThat(configurationFacade.getProjectConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);
      systemDataStorage.dropCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);

      // #2 if the system user collection is filled and key exists
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.PROJECT);
      configurationFacade.setProjectConfigurationString(DBURL_KEY, DUMMY_VALUE);
      assertThat(configurationFacade.getProjectConfigurationString(DBURL_KEY).get()).isEqualTo(DUMMY_VALUE);
   }

   @Test
   public void testSetAndGetProjectConfigurationInteger() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);

      configurationFacade.setProjectConfigurationInteger(PORT_KEY, DUMMY_PORT_VALUE);
      assertThat(configurationFacade.getProjectConfigurationInteger(PORT_KEY).get()).isEqualTo(DUMMY_PORT_VALUE);
   }

   @Test
   public void testSetAndGetProjectConfigurationDocument() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.PROJECT_CONFIG_COLLECTION);

      final DataDocument dummyDocument = ConfigurationManipulatorIntegrationTest.createDummyDataDocument();
      configurationFacade.setProjectConfigurationDocument(DOCUMENT_PROPERTY_KEY, dummyDocument);
      final DataDocument document = configurationFacade.getProjectConfigurationDocument(DOCUMENT_PROPERTY_KEY).get();

      assertThat(dummyDocument).isEqualTo(document);
   }

   @Test
   public void testSetAndGetOrganisationConfigurationString() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #1 if the system user collection is empty
      configurationFacade.setOrganisationConfigurationString(DBHOST_KEY, DUMMY_DBHOST_VALUE);
      assertThat(configurationFacade.getOrganisationConfigurationString(DBHOST_KEY).get()).isEqualTo(DUMMY_DBHOST_VALUE);
      systemDataStorage.dropCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      // #2 if the system user collection is filled and key exists
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.ORGANISATION);
      configurationFacade.setOrganisationConfigurationString(DBURL_KEY, DUMMY_VALUE);
      assertThat(configurationFacade.getOrganisationConfigurationString(DBURL_KEY).get()).isEqualTo(DUMMY_VALUE);
   }

   @Test
   public void testSetAndGetOrganisationConfigurationInteger() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      configurationFacade.setOrganisationConfigurationInteger(PORT_KEY, DUMMY_PORT_VALUE);
      assertThat(configurationFacade.getOrganisationConfigurationInteger(PORT_KEY).get()).isEqualTo(DUMMY_PORT_VALUE);
   }

   @Test
   public void testSetAndGetOrganisationConfigurationDocument() throws Exception {
      systemDataStorage.createCollection(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION);

      final DataDocument dummyDocument = ConfigurationManipulatorIntegrationTest.createDummyDataDocument();
      configurationFacade.setOrganisationConfigurationDocument(DOCUMENT_PROPERTY_KEY, dummyDocument);
      final DataDocument document = configurationFacade.getOrganisationConfigurationDocument(DOCUMENT_PROPERTY_KEY).get();

      assertThat(dummyDocument).isEqualTo(document);
   }

   @Test
   public void testResetUserConfiguration() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      final String id = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId() + "/" + userFacade.getUserEmail();

      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.USER_CONFIG_COLLECTION, id).get().get(CONFIG_DOCUMENT_KEY))).hasSize(BEFORE_SIZE_RESET);
      configurationFacade.resetUserConfiguration();
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.USER_CONFIG_COLLECTION, id).get().get(CONFIG_DOCUMENT_KEY))).hasSize(AFTER_SIZE_RESET);
   }

   @Test
   public void testResetUserConfigurationAttribute() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.USER);
      final String id = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId() + "/" + userFacade.getUserEmail();

      configurationFacade.resetUserConfigurationAttribute(DBURL_KEY);
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.USER_CONFIG_COLLECTION, id).get().get(CONFIG_DOCUMENT_KEY))).doesNotContainKey(DBURL_KEY);
   }

   @Test
   public void testResetProjectConfiguration() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.PROJECT);

      final String key = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId();

      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.PROJECT_CONFIG_COLLECTION, key).get().get(CONFIG_DOCUMENT_KEY))).hasSize(BEFORE_SIZE_RESET);
      configurationFacade.resetProjectConfiguration();
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.PROJECT_CONFIG_COLLECTION, key).get().get(CONFIG_DOCUMENT_KEY))).hasSize(AFTER_SIZE_RESET);
   }

   @Test
   public void testResetProjectConfigurationAttribute() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.PROJECT);

      final String key = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId();

      configurationFacade.resetProjectConfigurationAttribute(DBURL_KEY);
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.PROJECT_CONFIG_COLLECTION, key).get().get(CONFIG_DOCUMENT_KEY))).doesNotContainKey(DBURL_KEY);
   }

   @Test
   public void testResetOrganisationConfiguration() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.ORGANISATION);

      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION, organisationFacade.getOrganisationId()).get().get(CONFIG_DOCUMENT_KEY))).hasSize(BEFORE_SIZE_RESET);
      configurationFacade.resetOrganisationConfiguration();
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION, organisationFacade.getOrganisationId()).get().get(CONFIG_DOCUMENT_KEY))).hasSize(AFTER_SIZE_RESET);
   }

   @Test
   public void testResetOrganisationConfigurationAttribute() throws Exception {
      fillSystemCollection(ConfigurationFacade.ConfigurationLevel.ORGANISATION);

      configurationFacade.resetOrganisationConfigurationAttribute(DBURL_KEY);
      assertThat(((DataDocument) configurationManipulator.getConfigurationEntry(ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION, organisationFacade.getOrganisationId()).get().get(CONFIG_DOCUMENT_KEY))).doesNotContainKey(DBURL_KEY);
   }

   private void fillSystemCollection(final ConfigurationFacade.ConfigurationLevel level) {
      final String collectionName;
      final String nameKeyValue;

      switch (level) {
         case USER:
            collectionName = ConfigurationFacade.USER_CONFIG_COLLECTION;
            nameKeyValue = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId() + "/" + userFacade.getUserEmail();
            break;
         case PROJECT:
            collectionName = ConfigurationFacade.PROJECT_CONFIG_COLLECTION;
            nameKeyValue = organisationFacade.getOrganisationId() + "/" + projectFacade.getCurrentProjectId();
            break;
         case ORGANISATION:
            collectionName = ConfigurationFacade.ORGANISATION_CONFIG_COLLECTION;
            nameKeyValue = organisationFacade.getOrganisationId();
            break;
         default:
            return; // never happens but we can compile
      }

      systemDataStorage.createCollection(collectionName);
      // insert user entries
      final DataDocument insertedDocument = new DataDocument();
      insertedDocument.put(ConfigurationManipulator.NAME_KEY, nameKeyValue);

      final DataDocument config = new DataDocument();
      config.put(DBHOST_KEY, DUMMY_DBHOST_VALUE);
      config.put(PORT_KEY, DUMMY_PORT_VALUE);
      config.put(DBURL_KEY, DUMMY_DBURL_VALUE);
      config.put(DOCUMENT_PROPERTY_KEY, ConfigurationManipulatorIntegrationTest.createDummyDataDocument());

      insertedDocument.put(CONFIG_DOCUMENT_KEY, config);

      systemDataStorage.createDocument(collectionName, insertedDocument);
   }

   private boolean isDatabaseCollection(final String collectionName) {
      return systemDataStorage.hasCollection(collectionName);
   }

}