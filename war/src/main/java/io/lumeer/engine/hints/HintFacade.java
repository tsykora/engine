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
package io.lumeer.engine.hints;

import io.lumeer.engine.api.data.DataStorage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

/**
 * @author <a href="mailto:kotrady.johnny@gmail.com">Jan Kotrady</a>
 */
@SessionScoped
public class HintFacade implements Serializable {

   @Inject
   HintExecutor hintEx;

   private List<Future<Hint>> hints = new ArrayList<>();
   public void getHint() throws ExecutionException, InterruptedException {
      Hint hint = null;
      if (!hints.isEmpty())
         for(Future<Hint> future : hints)
         {
            if (future.isDone()){
               hint = future.get();
               hints.remove(future);
               break;
            }
         }
      if (hint != null){
         hint.sendNotification();
      }
   }

   public void runHint(String hintName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
      Hint hint = (Hint)Class.forName("io.lumeer.engine.hints.DetectTypeHint").newInstance();
      hints.add(hintEx.runHintDetect(hint));
   }

   public boolean haveHint(){
      if (hints.isEmpty()){
         return false;
      }
      return true;
   }

   public void clearHints(){
      hints.clear();
   }

   public void clearOldHints(){
      //TODO
   }
}