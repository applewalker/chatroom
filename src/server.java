import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
public class server extends JFrame {
	public Map<String,PrintStream> clients=new Map ();  
	JPanel sp=new JPanel();
	JTextArea sta=new JTextArea();
	JScrollPane ssc=new JScrollPane();
public server(String title){//构造服务器面板记录信息
		super(title);
		this.setBounds(300, 250, 300, 320);
		this.add(sp);
		sta.setEditable(false);
		sta.setColumns(25);
		sta.setRows(15);
		ssc.setViewportView(sta);
		sp.add(ssc);
		ssc.setBounds(10, 50, 280, 300);
		setVisible(true);
		setResizable(false);
		this.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e){
		    	socketfu.send("服务器关闭"); 
		        System.out.println("服务器关闭");
		        System.exit(0);
		    }
		});
	}
class socketfunc extends ArrayList{
	 void send(String str){//输出公共信息
		Socket socket;
		PrintStream out =null;
		for(int i=0;i<size();i++){
			socket=(Socket)get(i);
			try{
				out=new PrintStream(socket.getOutputStream(),true);//创建输出流
				if(out!=null)
				out.println(str);//通过输出流写入信息
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	 void sendprivate(String user,String primsg){//输出私聊
		clients.get(user).println(primsg);
	}
	void sendnum() throws UnknownHostException{//输出客户端数量
		String num ="当前最大连接数"+size();
		sta.append(num+"\n");
		sta.setCaretPosition(sta.getText().length());//光标在最新显示部位
		System.out.println(num);
	}
}
socketfunc socketfu=new socketfunc();
public void getserver(){
	try{
		ServerSocket serverSocket=new ServerSocket(7000);
		sta.append("已创建\n");
		System.out.println("已创建");
		while(true){
			Socket socket=serverSocket.accept();//等待
			new myth(socket).start();//启动
			socketfu.add(socket);
			socketfu.sendnum();
		}
	}catch(Exception e){
		sta.append("未创建成功\n");
		sta.setCaretPosition(sta.getText().length());//光标在最新显示部位
		e.printStackTrace();
	}
	}
 class myth extends Thread{
	 BufferedReader re;
	 PrintStream ps;
	 Socket socket=null;
	 public myth(Socket socket) {
	 	this.socket=socket;
	 }
	 public void run(){
	 	try {
	 		re=new BufferedReader(new InputStreamReader(socket.getInputStream()));
	 		ps=new PrintStream(socket.getOutputStream());  
	 		String ss,usera,userb,primsg;
	 		while((ss=re.readLine())!=null){
	 			if(ss.startsWith("#"))//USERNAME信息
	 			{	
	 				if(clients.containsKey(ss.substring(1))) // 姓名重复
					{
	 					
						ps.println("#error01");//发送错误代码
					}
					else // 姓名不重复
					{	
						
						socketfu.send(">>  "+ss.substring(1)+"  已登录");
	 					sta.append(">>  "+ss.substring(1)+"  已登录"+"\n");
	 					clients.put(ss.substring(1), ps);
	 					socketfu.sendprivate(ss.substring(1), ">>  "+ss.substring(1)+"  已登录");
					}
	 			}
	 			else if(ss.startsWith("@"))//私聊信息
	 			{
	 				usera=clients.getKeyByValue(ps);
	 				userb=ss.split("@")[1];
	 				primsg="##[私聊] "+usera+"@"+userb+": "+ss.split("@")[2];
	 				socketfu.sendprivate(usera,primsg);
	 				socketfu.sendprivate(userb,primsg);
	 				sta.append(primsg+"\n");
	 				System.out.println(primsg);
	 				sta.setCaretPosition(sta.getText().length());//光标在最新显示部位
	 			}  
	 			else{//公共聊天信息
	 				socketfu.send(ss);
	 				sta.append(ss+"\n");
	 				System.out.println(ss);
	 				sta.setCaretPosition(sta.getText().length());//光标在最新显示部位
	 			}	 				
	 				 } 

	 	} catch (IOException e) {
	 		clients.removeByValue(ps);
	 	}
	 }
}
 class Map<K,V> extends HashMap<K,V> {//采用了map建立一个用户和Socket的映射关系
		// 根据value来删除指定项
		public void removeByValue(Object value)
		{
			for(Object key :keySet())
			{
				if(get(key)==value||get(key).equals(value))
				{
					remove(key);
					break;
				}
			}
		}
		// 获取value集合
		public Set<V> valueSet()
		{
			Set<V> result=new HashSet<V>();
			for(Object key : keySet())
			{
				result.add(get(key));
			}
			return result;
		}
		// 重写HashMap的put方法，该方法不允许value重复
		public V put(K key,V value)
		{
			for(V val : valueSet())
			{
				if(val==value||val.equals(value))
				{
					throw new RuntimeException("不允许有重复value");
				}
			}
			return super.put(key, value);	
		}
		
		// 通过value查找key
		public K getKeyByValue(Object value)
		{
			for(K key : keySet())
			{
				if(get(key)==value||get(key).equals(value))
				{
					return key;
				}
			}
			return null;
		}
	}
 public static void main(String[] args){
	 (new server("服务器")).getserver();
	 
 }
}
