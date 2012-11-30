/*
 * Sonar Erlang Plugin
 * Copyright (C) 2012 Tamas Kende
 * kende.tamas@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.erlang.checks;

import org.sonar.erlang.api.ErlangGrammar;
import org.sonar.erlang.api.ErlangMetric;

import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.squid.checks.SquidCheck;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Cardinality;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.check.RuleProperty;
import org.sonar.squid.api.SourceFunction;

@Rule(key = "BranchesOfRecursion", priority = Priority.MAJOR, cardinality = Cardinality.SINGLE)
@BelongsToProfile(title = CheckList.REPOSITORY_NAME, priority = Priority.MAJOR)
public class BranchesOfRecursionCheck extends SquidCheck<ErlangGrammar> {

    private static final int DEFAULT_MAXIMUM_BOR_THRESHOLD = 10;

    @RuleProperty(key = "maximumBORThreshold", defaultValue = "" + DEFAULT_MAXIMUM_BOR_THRESHOLD)
    private int maximumBORThreshold = DEFAULT_MAXIMUM_BOR_THRESHOLD;

    @Override
    public void init() {
        subscribeTo(getContext().getGrammar().functionDeclaration);
    }

    @Override
    public void leaveNode(AstNode node) {
        SourceFunction function = (SourceFunction) getContext().peekSourceCode();
        if (function.getInt(ErlangMetric.BRANCHES_OF_RECURSION) > maximumBORThreshold) {
            getContext()
                    .createLineViolation(
                            this,
                            "Function has {0,number,integer} branches of recursion which is greater than {1,number,integer} authorized.",
                            node, function.getInt(ErlangMetric.BRANCHES_OF_RECURSION),
                            maximumBORThreshold);
        }
    }

    public void setMaximumBORThreshold(int threshold) {
        this.maximumBORThreshold = threshold;
    }

}
