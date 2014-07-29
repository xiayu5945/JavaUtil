package web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
  
public class LuceneDemo {  
	private static final Operator OPERATOR = QueryParser.AND_OPERATOR;
	private static final Version MATCH_VERSHION = Version.LUCENE_4_9;
    public static void createIndex(List<Document> documentList, String indexPath) {  
        if(null == documentList || documentList.isEmpty()){
        	return;
        }
        
        Directory directory = null;  
        try {  
            directory = new SimpleFSDirectory(new File(indexPath));  
            Analyzer analyzer = new StandardAnalyzer(MATCH_VERSHION); 
            IndexWriterConfig config = new IndexWriterConfig(MATCH_VERSHION, analyzer);  
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);  
            IndexWriter indexWriter = new IndexWriter(directory, config);  
            indexWriter.addDocuments(documentList);
            indexWriter.commit(); 
            indexWriter.close();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }
    /** 
     * 搜索 
     *  
     * @throws ParseException 
     * @throws IOException 
     * @throws InvalidTokenOffsetsException 
     */  
    public static List<Document> searchIndex(String keyword, String[] fields,int topN, String indexPath) {  
        Analyzer analyzer = new StandardAnalyzer(MATCH_VERSHION);  
        Directory directory = null;  
        try {  
            directory = FSDirectory.open(new File(indexPath));  
        } catch (Exception e) {  
            System.out.println("索引打开异常！");  
        }  
 
        QueryParser qp = new MultiFieldQueryParser(MATCH_VERSHION, fields, analyzer);  
        qp.setDefaultOperator(OPERATOR);  
        List<Document> docLsit = new LinkedList<Document>();
		try {
			Query query = qp.parse(keyword);  
			// 搜索相似度最高的5条记录  
			DirectoryReader ireader = DirectoryReader.open(directory);  
			IndexSearcher isearcher = new IndexSearcher(ireader); 
			TopDocs topDocs = isearcher.search(query, topN);  
			System.out.println("命中：" + topDocs.totalHits);  
			// 输出结果  
			ScoreDoc[] scoreDocs = topDocs.scoreDocs; 
			for (int i = 0; i < scoreDocs.length; i++) {  
			    Document targetDoc = isearcher.doc(scoreDocs[i].doc);
			    docLsit.add(targetDoc);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return docLsit;
    }  
  
    public static void main(String[] args) {
		StringField id = new StringField("id", "1",Store.YES );
		TextField content = new TextField("content", "中国人夺霜三个",Store.YES);
		StringField time = new StringField("time", "中国人", Store.YES);
		Document doc = new Document();
		doc.add(id);
		doc.add(content);
		doc.add(time);
		
		id = new StringField("id", "2",Store.YES );
		content = new TextField("content", "中国人",Store.YES);
		time = new StringField("time", "now2", Store.YES);
		Document doc2 = new Document();
		doc2.add(id);
		doc2.add(content);
		doc2.add(time);
		
		List<Document> documentSet = new LinkedList<Document>();
		documentSet.add(doc);
		documentSet.add(doc2);
		createIndex(documentSet , "F:\\luceneindex");
		List<Document> val = searchIndex("中国", new String[]{"content","id"}, 7, "F:\\luceneindex");
		int i=1;
		for(Document document: val){
			System.out.println("-----------"+i+"---------");
			System.out.println(document.get("content"));
			System.out.println(document.get("id"));
			System.out.println(document.get("time"));
			System.out.println(document.get("time22"));
			i++;
		}
		
	}
  private static String readFile(String filePath) throws Exception{
 	BufferedReader bufferedReader = new BufferedReader(
 	new InputStreamReader(new FileInputStream(filePath)));
 	StringBuffer content = new StringBuffer();
 	String str = null;
 	while ((str = bufferedReader.readLine()) != null) {
 		content.append(str).append("\n");
 	}
 	return content.toString();
  }

    
    public static Document creatDocument(Set<IndexableField> valueSet){
    	if(null == valueSet || valueSet.isEmpty())return null;
    	Document document = new Document();
    	Iterator<IndexableField> it = valueSet.iterator();
    	while(it.hasNext()){
    		IndexableField field = it.next();
    		document.add(field);
    	}
		return document;	
    }
    
    public static Set<Document> creatDocuments(List<Set<IndexableField>> valueLsit ){
    	if(null == valueLsit || valueLsit.isEmpty())return null;
    	Set<Document> documentSet = new TreeSet<Document>();
    	for(Set<IndexableField> fieldSet : valueLsit){
    		documentSet.add(creatDocument(fieldSet));
    	}
		return documentSet;
    	
    }
} 
