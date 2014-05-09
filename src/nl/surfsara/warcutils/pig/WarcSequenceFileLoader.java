/**
 * Copyright 2014 SURFsara
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfsara.warcutils.pig;

import java.io.IOException;

import nl.surfsara.warcutils.WarcSequenceFileInputFormat;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.pig.LoadFunc;
import org.apache.pig.backend.hadoop.executionengine.mapReduceLayer.PigSplit;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.TupleFactory;
import org.jwat.warc.WarcRecord;

/**
 * Pig load function for warc, wat and wet files that have been converted to
 * sequencefiles. Values from the reader are returned as WarcRecords from the
 * Java Web Archive Toolkit.
 * 
 * @author mathijs.kattenberg@surfsara.nl
 */
public class WarcSequenceFileLoader extends LoadFunc {

	private RecordReader<LongWritable, WarcRecord> in;
	private TupleFactory mTupleFactory = TupleFactory.getInstance();

	@Override
	public InputFormat<LongWritable, WarcRecord> getInputFormat() throws IOException {
		return new WarcSequenceFileInputFormat();
	}

	@Override
	public Tuple getNext() throws IOException {
		WarcRecord warcRecord = null;
		try {
			if (in.nextKeyValue()) {
				warcRecord = in.getCurrentValue();

				/*
				 * You can expand this loader by returning values from
				 * the warcRecord as needed.  
				 */
				Tuple t = mTupleFactory.newTuple(1);
				t.set(0, warcRecord.header.contentTypeStr);

				return t;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void prepareToRead(RecordReader reader, PigSplit arg1) throws IOException {
		in = reader;
	}

	@Override
	public void setLocation(String location, Job job) throws IOException {
		FileInputFormat.setInputPaths(job, location);
	}
}
