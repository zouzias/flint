/*
 *  Copyright 2015-2016 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.twosigma.flint.timeseries.summarize.summarizer

import com.twosigma.flint.rdd.function.summarize.summarizer.subtractable
import com.twosigma.flint.timeseries.Schema

import org.apache.spark.sql.catalyst.expressions.GenericInternalRow
import org.apache.spark.sql.catalyst.util.GenericArrayData
import org.apache.spark.sql.types._

case class RowsSummarizerFactory(column: String) extends SummarizerFactory {
  override def apply(inputSchema: StructType): RowsSummarizer = RowsSummarizer(inputSchema, alias, column)
}

case class RowsSummarizer(
  override val inputSchema: StructType,
  override val alias: Option[String],
  column: String
) extends LeftSubtractableSummarizer {
  override type T = GenericInternalRow
  override type U = Vector[GenericInternalRow]
  override type V = Vector[GenericInternalRow]
  override val summarizer = subtractable.RowsSummarizer[GenericInternalRow]()
  override val schema = Schema.of(column -> ArrayType(inputSchema))

  override def toT(r: GenericInternalRow): T = r

  override def fromV(v: V): GenericInternalRow = {
    val values = new GenericArrayData(v.toArray)
    new GenericInternalRow(Array[Any](values))
  }
}
