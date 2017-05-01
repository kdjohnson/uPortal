/**
 * Licensed to Apereo under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright ownership. Apereo
 * licenses this file to you under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at the
 * following location:
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apereo.portal.jpa;

import javax.persistence.EntityManager;

/**
 * Event fired immediately after the {@link EntityManager} is created
 *
 * @author Eric Dalquist
 */
public class EntityManagerCreatedEvent extends AbstractEntityManagerEvent {
    private static final long serialVersionUID = 1L;

    public EntityManagerCreatedEvent(
            Object source,
            long entityManagerId,
            String persistenceUnitName,
            EntityManager entityManager) {
        super(source, entityManagerId, persistenceUnitName, entityManager);
    }

    @Override
    public String toString() {
        return "EntityManagerCreatedEvent [entityManagerId=" + getEntityManagerId() + "]";
    }
}
