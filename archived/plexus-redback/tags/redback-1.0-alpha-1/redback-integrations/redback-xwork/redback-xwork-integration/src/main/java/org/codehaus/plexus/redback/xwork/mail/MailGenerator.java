package org.codehaus.plexus.redback.xwork.mail;

/*
 * Copyright 2005-2006 The Codehaus.
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
 */

import org.codehaus.plexus.redback.keys.AuthenticationKey;

/**
 * Mail generator component.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id: Mailer.java 5591 2007-02-06 09:09:50Z evenisse $
 */
public interface MailGenerator
{
    String ROLE = MailGenerator.class.getName();

    String generateMail( String templateName, AuthenticationKey authkey, String baseUrl );
}
