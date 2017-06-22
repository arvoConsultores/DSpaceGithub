--
-- The contents of this file are subject to the license and copyright
-- detailed in the LICENSE and NOTICE files at the root of the source
-- tree and available online at
--
-- http://www.dspace.org/license/
--

------------------------------------------------------
-- Column with the personal Orcid
------------------------------------------------------

ALTER TABLE eperson
   ADD COLUMN orcid VARCHAR2(32);