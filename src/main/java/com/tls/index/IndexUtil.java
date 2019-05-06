package com.tls.index;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

@SuppressWarnings("all")
public class IndexUtil {
	private String[] ids = { "1", "2", "3", "4", "5", "6" };
	private String[] emails = { "aa@itat.org", "bb@itat.org", "cc@cc.org", "dd@sina.org", "ee@zttc.edu",
			"ff@itat.org" };
	private String[] contents = { "welcome to visited the space,I like book", "hello boy, I like pingpeng ball",
			"my name is cc I like game", "I like football", "I like football and I like basketball too",
			"I like movie and swim" };
	private Date[] dates = null;
	private int[] attachs = { 2, 3, 1, 4, 5, 5 };
	private String[] names = { "zhangsan", "lisi", "john", "jetty", "mike", "jake" };
	private Directory directory = null;
	private Map<String, Float> scores = new HashMap<String, Float>();
	private static IndexReader reader = null;

	public IndexUtil() {
		try {
			setDates();
			scores.put("itat.org",2.0f);
			scores.put("zttc.edu", 1.5f);
			directory = FSDirectory.open(new File("d:/lucene/index02"));
			reader = IndexReader.open(directory,false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void setDates() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dates = new Date[ids.length];
			dates[0] = sdf.parse("2010-02-19");
			dates[1] = sdf.parse("2012-01-11");
			dates[2] = sdf.parse("2011-09-19");
			dates[3] = sdf.parse("2010-12-22");
			dates[4] = sdf.parse("2012-01-01");
			dates[5] = sdf.parse("2011-05-19");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IndexSearcher getSearcher() {
		try {
			if(reader==null) {
				reader = IndexReader.open(directory,false);
			} else {
				IndexReader tr = IndexReader.openIfChanged(reader);
				if(tr!=null) {
					reader.close();
					reader = tr;
				}
			}
			return new IndexSearcher(reader);
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
	}

	public void query() {
		try {
			IndexReader reader = IndexReader.open(directory);
			// 通过reader可以有效的获取到文档的数量
			System.out.println("numDocs:" + reader.numDocs());
			System.out.println("maxDocs:" + reader.maxDoc());
			System.out.println("deleteDocs:" + reader.numDeletedDocs());
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void delete() {
		try {
			IndexWriter writer = new IndexWriter(directory,
					new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			// 参数是一个选项，可以是一个Query，也可以是一个term，term是一个精确查找的值
			// 此时删除的文档并不会被完全删除，而是存储在一个回收站中的，可以恢复
			writer.deleteDocuments(new Term("id", "1"));
			writer.commit();
			writer.close();
		} catch (Exception e) {
		}
	}
	
	public void delete02() {
		try {
			reader.deleteDocuments(new Term("id","1"));
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void undelete() {
		// 使用IndexReader进行恢复
		try {
			IndexReader reader = IndexReader.open(directory, false);
			// 恢复时，必须把IndexReader的只读(readOnly)设置为false
			reader.undeleteAll();
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void forceDelete() {
		IndexWriter writer = null;

		try {
			writer = new IndexWriter(directory,
					new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.forceMergeDeletes();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void merge() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory,
					new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			// 会将索引合并为2段，这两段中的被删除的数据会被清空
			// 特别注意：此处Lucene在3.5之后不建议使用，因为会消耗大量的开销，
			// Lucene会根据情况自动处理的
			writer.forceMerge(2);
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(directory,
					new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			/*
			 * Lucene并没有提供更新，这里的更新操作其实是如下两个操作的合集 先删除之后再添加
			 */
			Document doc = new Document();
			doc.add(new Field("id", "11", Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			doc.add(new Field("email", emails[0], Field.Store.YES, Field.Index.NOT_ANALYZED));
			doc.add(new Field("content", contents[0], Field.Store.NO, Field.Index.ANALYZED));
			doc.add(new Field("name", names[0], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
			writer.updateDocument(new Term("id", "1"), doc);
			writer.commit();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void index() {
		try {
			IndexWriter writer = null;
			writer = new IndexWriter(directory,
					new IndexWriterConfig(Version.LUCENE_35, new StandardAnalyzer(Version.LUCENE_35)));
			writer.deleteAll();
			writer.commit();
			Document doc = null;
			for (int i = 0; i < ids.length; i++) {
				doc = new Document();
				doc.add(new Field("id", ids[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				doc.add(new Field("email", emails[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
//				doc.add(new Field("email", "test" + i + "@test.com", Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("content", contents[i], Field.Store.NO, Field.Index.ANALYZED));
				doc.add(new Field("name", names[i], Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
				//存储数字
				doc.add(new NumericField("attach",Field.Store.YES,true).setIntValue(attachs[i]));
				//存储日期
				doc.add(new NumericField("date",Field.Store.YES,true).setLongValue(dates[i].getTime()));
				String et = emails[i].substring(emails[i].lastIndexOf("@")+1);
				System.out.println(et);
				if(scores.containsKey(et)) {
					doc.setBoost(scores.get(et));
				} else {
					doc.setBoost(0.5f);
				}
				writer.addDocument(doc);
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void queryEmail(){
		try {
			// 创建IndexReader
			IndexReader reader = IndexReader.open(directory);
			// 根据IndexReader创建IndexSearcher
			IndexSearcher searcher = new IndexSearcher(reader);
			// 创建搜索的Query
			QueryParser parser = new QueryParser(Version.LUCENE_35, "content", new StandardAnalyzer(Version.LUCENE_35));
			Query query = parser.parse("visited");
			// 根据searcher搜索并返回TopDocs
			TopDocs tds = searcher.search(query, 10);
			// 根据TopDocs获取ScoreDoc对象
			ScoreDoc[] sds = tds.scoreDocs;
			for (ScoreDoc sd : sds) {
				// 根据searcher和ScoreDoc对象获取具体的Document对象
				Document d = searcher.doc(sd.doc);
				// 根据Document对象获取需要的值
				System.out.println(d.get("id"));
			}
			// 关闭reader
			searcher.close();
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void search01() {
		try {
			IndexReader reader = IndexReader.open(directory);
			IndexSearcher searcher = new IndexSearcher(reader);
			TermQuery query = new TermQuery(new Term("content","like"));
			TopDocs tds = searcher.search(query, 10);
			for(ScoreDoc sd:tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("("+sd.doc+"-"+doc.getBoost()+"-"+sd.score+")"+
						doc.get("name")+"["+doc.get("email")+"]-->"+doc.get("id")+","+
						doc.get("attach")+","+doc.get("date")+","+doc.getValues("email"));
			}
			reader.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void search02() {
		try {
			IndexSearcher searcher = getSearcher();
			TermQuery query = new TermQuery(new Term("content","like"));
			TopDocs tds = searcher.search(query, 10);
			for(ScoreDoc sd:tds.scoreDocs) {
				Document doc = searcher.doc(sd.doc);
				System.out.println("("+sd.doc+"-"+doc.getBoost()+"-"+sd.score+")"+
						doc.get("name")+"["+doc.get("email")+"]-->"+doc.get("id")+","+
						doc.get("attach")+","+doc.get("date")+","+doc.getValues("email"));
			}
			searcher.close();
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
