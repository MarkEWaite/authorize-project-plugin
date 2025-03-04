/*
 * The MIT License
 *
 * Copyright (c) 2013 IKEDA Yasuyuki
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.authorizeproject;

import hudson.XmlFile;
import hudson.model.Descriptor;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest2;

/**
 * Base {@link Descriptor} class for {@link AuthorizeProjectStrategy} instances.
 */
public abstract class AuthorizeProjectStrategyDescriptor extends Descriptor<AuthorizeProjectStrategy> {

    protected AuthorizeProjectStrategyDescriptor() {
        super();
    }

    protected AuthorizeProjectStrategyDescriptor(Class<? extends AuthorizeProjectStrategy> clazz) {
        super(clazz);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final XmlFile getConfigFile() {
        return new XmlFile(new File(Jenkins.get().getRootDir(), Constants.CONFIG_FOLDER + "/" + getId() + ".xml"));
    }

    /**
     * @return descriptors with configuration views in "Configure Global Security" as a child of {@link ProjectQueueItemAuthenticator}.
     */
    public String getGlobalSecurityConfigPage() {
        for (String cand : getPossibleViewNames("global-security")) {
            String page = getViewPage(clazz, cand);
            // Unfortunately, Descriptor#getViewPage returns passed value
            // when that view is not found.
            // When found, path to that file is returned,
            // so I can check whether found
            // by comparing passing value and returned value.
            if (page != null && !page.equals(cand)) {
                return page;
            }
        }
        return null;
    }

    /**
     * @return descriptors to display page in "Configure Global Security" as a child of {@link ProjectQueueItemAuthenticator}.
     */
    public static List<AuthorizeProjectStrategyDescriptor> getDescriptorsForGlobalSecurityConfigPage() {
        List<Descriptor<AuthorizeProjectStrategy>> all = AuthorizeProjectStrategy.all();
        List<AuthorizeProjectStrategyDescriptor> r = new ArrayList<>(all.size());
        for (Descriptor<AuthorizeProjectStrategy> d : all) {
            if (d instanceof AuthorizeProjectStrategyDescriptor descriptor
                    && descriptor.getGlobalSecurityConfigPage() != null) {
                r.add(descriptor);
            }
        }
        return r;
    }

    /**
     * Invoked when configuration is submitted from "Configure Global Security" as a child of {@link ProjectQueueItemAuthenticator}.
     * You should call save() by yourself.
     */
    public void configureFromGlobalSecurity(StaplerRequest2 req, JSONObject js) throws FormException {}

    /**
     * @return this strategy can be enabled by default.
     */
    public boolean isEnabledByDefault() {
        return true;
    }

    /**
     * @return whether configurable for {@link GlobalQueueItemAuthenticator}
     * @since 1.2.0
     */
    public boolean isApplicableToGlobal() {
        return true;
    }
}
