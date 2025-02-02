/*
 * Copyright 2020 Alibaba Group Holding Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.graphscope.common.ir.rel;

import com.alibaba.graphscope.common.ir.rel.type.order.GraphFieldCollation;
import com.google.common.collect.ImmutableList;

import org.apache.calcite.plan.RelOptCluster;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelCollation;
import org.apache.calcite.rel.RelFieldCollation;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.rel.core.Sort;
import org.apache.calcite.rel.hint.RelHint;
import org.apache.calcite.rex.RexNode;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class GraphLogicalSort extends Sort {
    protected GraphLogicalSort(
            RelOptCluster cluster,
            RelTraitSet traits,
            List<RelHint> hints,
            RelNode child,
            RelCollation collation,
            @Nullable RexNode offset,
            @Nullable RexNode fetch) {
        super(cluster, traits, hints, child, collation, offset, fetch);
    }

    public static GraphLogicalSort create(
            RelNode input,
            RelCollation collation,
            @Nullable RexNode offset,
            @Nullable RexNode fetch) {
        return new GraphLogicalSort(
                input.getCluster(),
                RelTraitSet.createEmpty(),
                ImmutableList.of(),
                input,
                collation,
                offset,
                fetch);
    }

    @Override
    public List<RexNode> getSortExps() {
        RelCollation collation = getCollation();
        List<RelFieldCollation> fieldCollations = collation.getFieldCollations();
        return fieldCollations.stream()
                .map(k -> ((GraphFieldCollation) k).getVariable())
                .collect(Collectors.toList());
    }

    @Override
    public Sort copy(
            RelTraitSet traitSet,
            RelNode newInput,
            RelCollation newCollation,
            @Nullable RexNode offset,
            @Nullable RexNode fetch) {
        return new GraphLogicalSort(
                getCluster(), traitSet, hints, newInput, newCollation, offset, fetch);
    }
}
