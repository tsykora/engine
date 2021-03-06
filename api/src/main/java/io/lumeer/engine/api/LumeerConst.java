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
package io.lumeer.engine.api;

import java.util.Arrays;
import java.util.List;

/**
 * Lumeer constants.
 *
 * @author <a href="mailto:marvenec@gmail.com">Martin Večeřa</a>
 */
public final class LumeerConst {

   public static final String LUMEER_VERSION = "1.0";

   public static final String DB_HOST_PROPERTY = "db_host";
   public static final String DB_PORT_PROPERTY = "db_port";
   public static final String DB_NAME_PROPERTY = "db_name";
   public static final String DB_USER_PROPERTY = "db_user";
   public static final String DB_PASSWORD_PROPERTY = "db_passwd";
   public static final String DB_USE_SSL = "db_ssl";

   public static final String SYSTEM_DB_HOST_PROPERTY = "sys_db_host";
   public static final String SYSTEM_DB_PORT_PROPERTY = "sys_db_port";
   public static final String SYSTEM_DB_NAME_PROPERTY = "sys_db_name";
   public static final String SYSTEM_DB_USER_PROPERTY = "sys_db_user";
   public static final String SYSTEM_DB_PASSWORD_PROPERTY = "sys_db_passwd";
   public static final String SYSTEM_DB_USE_SSL = "sys_db_ssl";

   public static final String USER_LOCALE_PROPERTY = "locale";

   public static final String DEFAULT_LIMIT_PROPERTY = "result_limit";

   private LumeerConst() {
      // we do not want any instances to be created
      throw new UnsupportedOperationException(String.format("Creation of %s is forbidden.", this.getClass().getCanonicalName()));
   }

   public static class Linking {
      public static final String PREFIX = "_linking";

      /**
       * Which direction of link to work with.
       */
      public enum LinkDirection {
         BOTH, FROM, TO;

         public static LinkDirection fromString(final String s) {
            return LinkDirection.valueOf(s);
         }
      }

      public class MainTable {
         public static final String NAME = "_system-linking";
         public static final String ATTR_FROM_COLLECTION = "from_collection";
         public static final String ATTR_TO_COLLECTION = "to_collection";
         public static final String ATTR_COL_NAME = "collection_name";
         public static final String ATTR_ROLE = "role";
      }

      public class LinkingTable {
         public static final String ATTR_FROM_ID = "from_id";
         public static final String ATTR_TO_ID = "to_id";
         public static final String ATTR_ATTRIBUTES = "attributes";
      }
   }

   public static class Document {
      public static final String ID = "_id";
      public static final String METADATA_PREFIX = "_meta-";
      public static final String CREATE_DATE_KEY = METADATA_PREFIX + "create-date";
      public static final String UPDATE_DATE_KEY = METADATA_PREFIX + "update-date";
      public static final String CREATE_BY_USER_KEY = METADATA_PREFIX + "create-user";
      public static final String UPDATED_BY_USER_KEY = METADATA_PREFIX + "update-user";
      public static final String USER_RIGHTS = METADATA_PREFIX + "rights";
      public static final String COLLECTION_NAME = METADATA_PREFIX + "collection"; // used in cases where we need to note the source collection in the document
      public static final String METADATA_VERSION_KEY = METADATA_PREFIX + "version";
      public static final List<String> METADATA_KEYS = Arrays.asList(METADATA_VERSION_KEY, CREATE_DATE_KEY, UPDATE_DATE_KEY, CREATE_BY_USER_KEY, UPDATED_BY_USER_KEY, USER_RIGHTS);
   }

   public static class View {
      public static final String VIEW_METADATA_COLLECTION_NAME = "viewmetadatacollection";
      public static final String VIEW_SEQUENCE_NAME = "view-sequence";

      public static final String VIEW_NAME_KEY = "name";
      public static final String VIEW_ID_KEY = "view-id";
      public static final String VIEW_TYPE_KEY = "type";
      public static final String VIEW_TYPE_DEFAULT_VALUE = "default";
      public static final String VIEW_CONFIGURATION_KEY = "configuration";

      public static final String VIEW_USER_RIGHTS_KEY = Document.USER_RIGHTS;
      //public static final String VIEW_GROUP_RIGHTS_KEY = "group-rights";
      public static final String VIEW_CREATE_DATE_KEY = Document.CREATE_DATE_KEY;
      public static final String VIEW_CREATE_USER_KEY = Document.CREATE_BY_USER_KEY;
      public static final String VIEW_UPDATE_DATE_KEY = Document.UPDATE_DATE_KEY;
      public static final String VIEW_UPDATE_USER_KEY = Document.UPDATED_BY_USER_KEY;
   }

   public static class Collection {
      public static final String META_TYPE_KEY = "meta-type";
      public static final String COLLECTION_NAME_PREFIX = "collection.";
      public static final String COLLECTION_METADATA_PREFIX = "meta.";

      public static final String COLLECTION_ATTRIBUTES_META_TYPE_VALUE = "attributes";

      public static final String COLLECTION_ATTRIBUTE_NAME_KEY = "attribute-name";
      public static final String COLLECTION_ATTRIBUTE_TYPE_KEY = "attribute-type";

      public static final String COLLECTION_ATTRIBUTE_TYPE_INT = "int";
      public static final String COLLECTION_ATTRIBUTE_TYPE_LONG = "long";
      public static final String COLLECTION_ATTRIBUTE_TYPE_DOUBLE = "double";
      public static final String COLLECTION_ATTRIBUTE_TYPE_DECIMAL = "decimal";
      public static final String COLLECTION_ATTRIBUTE_TYPE_DATE = "date"; // we accept yyyy.MM.dd HH.mm.ss.SSS as default
      public static final String COLLECTION_ATTRIBUTE_TYPE_BOOLEAN = "bool";
      public static final List<String> COLLECTION_ATTRIBUTE_TYPE_BOOLEAN_VALUES = Arrays.asList("true", "false");
      public static final String COLLECTION_ATTRIBUTE_TYPE_STRING = "string";
      public static final String COLLECTION_ATTRIBUTE_TYPE_LIST = "list";
      public static final String COLLECTION_ATTRIBUTE_TYPE_NESTED = "nested";
      public static final List<String> COLLECTION_ATTRIBUTE_TYPE_VALUES = Arrays.asList(
            COLLECTION_ATTRIBUTE_TYPE_INT,
            COLLECTION_ATTRIBUTE_TYPE_LONG,
            COLLECTION_ATTRIBUTE_TYPE_DOUBLE,
            COLLECTION_ATTRIBUTE_TYPE_DECIMAL,
            COLLECTION_ATTRIBUTE_TYPE_DATE,
            COLLECTION_ATTRIBUTE_TYPE_BOOLEAN,
            COLLECTION_ATTRIBUTE_TYPE_STRING,
            COLLECTION_ATTRIBUTE_TYPE_LIST,
            COLLECTION_ATTRIBUTE_TYPE_NESTED);

      public static final String COLLECTION_ATTRIBUTE_CONSTRAINTS_KEY = "attribute-constraints";
      public static final String COLLECTION_ATTRIBUTE_COUNT_KEY = "attribute-count";

      public static final String COLLECTION_REAL_NAME_META_TYPE_VALUE = "name";
      public static final String COLLECTION_REAL_NAME_KEY = "name";

      public static final String COLLECTION_LOCK_META_TYPE_VALUE = "lock";
      public static final String COLLECTION_LOCK_UPDATED_KEY = "updated";

      public static final String COLLECTION_RIGHTS_META_TYPE_VALUE = "rights";
      public static final String COLLECTION_CREATE_USER_KEY = Document.CREATE_BY_USER_KEY;
      public static final String COLLECTION_CREATE_DATE_KEY = Document.CREATE_DATE_KEY;

      public static final String COLLECTION_CUSTOM_META_TYPE_VALUE = "custom";

      public static final String COLLECTION_SHADOW_PREFFIX = "_shadow";
      public static final String COLLECTION_TRASH_PREFFIX = "_trash";
   }

   public static class Security {
      public static final String RULE = "rule";
      public static final String USER_ID = "user_email";

      public static final int READ = 4;
      public static final int WRITE = 2;
      public static final int EXECUTE = 1;
   }
}
