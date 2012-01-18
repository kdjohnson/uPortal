/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portal.events.aggr.dao.jpa;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.apache.commons.lang.Validate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.jasig.portal.events.aggr.AcademicTermDetails;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.ReadableInstant;

/**
 * @author Eric Dalquist
 * @version $Revision$
 */
@Entity
@Table(name = "UP_ACADEMIC_TERM_DETAILS")
@SequenceGenerator(
        name="UP_ACADEMIC_TERM_DETAILS_GEN",
        sequenceName="UP_ACADEMIC_TERM_DETAILS_SEQ",
        allocationSize=1
    )
@TableGenerator(
        name="UP_ACADEMIC_TERM_DETAILS_GEN",
        pkColumnValue="UP_ACADEMIC_TERM_DETAILS_PROP",
        allocationSize=1
    )
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AcademicTermDetailsImpl implements AcademicTermDetails, Serializable {
    private static final long serialVersionUID = 1L;
   
    @SuppressWarnings("unused")
    @Id
    @GeneratedValue(generator = "UP_ACADEMIC_TERM_DETAILS_GEN")
    @Column(name="TERM_ID")
    private final long id;
    
    @NaturalId
    @Column(name="TERM_START", nullable=false)
    @Type(type="dateTime")
    private DateTime start;
    
    @NaturalId
    @Column(name="TERM_END", nullable=false)
    @Type(type="dateTime")
    private DateTime end;

    @Column(name="TERM_NAME", nullable=false)
    private String termName;
    
    @Transient
    private DateMidnight startDateMidnight;
    @Transient
    private DateMidnight endDateMidnight;
    
    @SuppressWarnings("unused")
    private AcademicTermDetailsImpl() {
        this.id = -1;
        this.start = null;
        this.end = null;
        this.termName = null;
    }
    
    public AcademicTermDetailsImpl(DateMidnight start, DateMidnight end, String termName) {
        Validate.notNull(start);
        Validate.notNull(end);
        Validate.notNull(termName);
        if (start.isEqual(end) || start.isBefore(end)) {
            throw new IllegalArgumentException("start cannot be before or equal to end");
        }
        this.id = -1;
        this.start = start.toDateTime();
        this.end = end.toDateTime();
        this.termName = termName;
        this.init();
    }
    
    @PostLoad
    @PostUpdate
    @PostPersist
    void init() {
        this.startDateMidnight = this.start.toDateMidnight();
        this.endDateMidnight = this.end.toDateMidnight();
    }

    @Override
    public String getTermName() {
        return this.termName;
    }
    
    @Override
    public void setTermName(String termName) {
        Validate.notNull(termName);
        this.termName = termName;
    }

    @Override
    public DateMidnight getStart() {
        return this.startDateMidnight;
    }
    
    @Override
    public void setStart(DateMidnight start) {
        this.startDateMidnight = start;
        this.start = start.toDateTime();
    }

    @Override
    public DateMidnight getEnd() {
        return this.endDateMidnight;
    }

    @Override
    public void setEnd(DateMidnight end) {
        this.endDateMidnight = end;
        this.end = end.toDateTime();
    }
    
    @Override
    public boolean contains(ReadableInstant instant) {
        return this.endDateMidnight.isAfter(instant) && (this.startDateMidnight.isBefore(instant) || this.startDateMidnight.isEqual(instant));
    }

    @Override
    public int compareTo(AcademicTermDetails o) {
        return this.getStart().compareTo(o.getStart());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.endDateMidnight == null) ? 0 : this.endDateMidnight.hashCode());
        result = prime * result + ((this.startDateMidnight == null) ? 0 : this.startDateMidnight.hashCode());
        result = prime * result + ((this.termName == null) ? 0 : this.termName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AcademicTermDetailsImpl other = (AcademicTermDetailsImpl) obj;
        if (this.endDateMidnight == null) {
            if (other.endDateMidnight != null)
                return false;
        }
        else if (!this.endDateMidnight.equals(other.endDateMidnight))
            return false;
        if (this.startDateMidnight == null) {
            if (other.startDateMidnight != null)
                return false;
        }
        else if (!this.startDateMidnight.equals(other.startDateMidnight))
            return false;
        if (this.termName == null) {
            if (other.termName != null)
                return false;
        }
        else if (!this.termName.equals(other.termName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AcademicTermDetailsImpl [termName=" + this.termName + ", startDateMidnight="
                + this.startDateMidnight + ", endDateMidnight=" + this.endDateMidnight + "]";
    }
}