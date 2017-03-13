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
public server(String title){//�������������¼��Ϣ
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
		    	socketfu.send("�������ر�"); 
		        System.out.println("�������ر�");
		        System.exit(0);
		    }
		});
	}
class socketfunc extends ArrayList{
	 void send(String str){//���������Ϣ
		Socket socket;
		PrintStream out =null;
		for(int i=0;i<size();i++){
			socket=(Socket)get(i);
			try{
				out=new PrintStream(socket.getOutputStream(),true);//���������
				if(out!=null)
				out.println(str);//ͨ�������д����Ϣ
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	 void sendprivate(String user,String primsg){//���˽��
		clients.get(user).println(primsg);
	}
	void sendnum() throws UnknownHostException{//����ͻ�������
		String num ="��ǰ���������"+size();
		sta.append(num+"\n");
		sta.setCaretPosition(sta.getText().length());//�����������ʾ��λ
		System.out.println(num);
	}
}
socketfunc socketfu=new socketfunc();
public void getserver(){
	try{
		ServerSocket serverSocket=new ServerSocket(7000);
		sta.append("�Ѵ���\n");
		System.out.println("�Ѵ���");
		while(true){
			Socket socket=serverSocket.accept();//�ȴ�
			new myth(socket).start();//����
			socketfu.add(socket);
			socketfu.sendnum();
		}
	}catch(Exception e){
		sta.append("δ�����ɹ�\n");
		sta.setCaretPosition(sta.getText().length());//�����������ʾ��λ
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
	 			if(ss.startsWith("#"))//USERNAME��Ϣ
	 			{	
	 				if(clients.containsKey(ss.substring(1))) // �����ظ�
					{
	 					
						ps.println("#error01");//���ʹ������
					}
					else // �������ظ�
					{	
						
						socketfu.send(">>  "+ss.substring(1)+"  �ѵ�¼");
	 					sta.append(">>  "+ss.substring(1)+"  �ѵ�¼"+"\n");
	 					clients.put(ss.substring(1), ps);
	 					socketfu.sendprivate(ss.substring(1), ">>  "+ss.substring(1)+"  �ѵ�¼");
					}
	 			}
	 			else if(ss.startsWith("@"))//˽����Ϣ
	 			{
	 				usera=clients.getKeyByValue(ps);
	 				userb=ss.split("@")[1];
	 				primsg="##[˽��] "+usera+"@"+userb+": "+ss.split("@")[2];
	 				socketfu.sendprivate(usera,primsg);
	 				socketfu.sendprivate(userb,primsg);
	 				sta.append(primsg+"\n");
	 				System.out.println(primsg);
	 				sta.setCaretPosition(sta.getText().length());//�����������ʾ��λ
	 			}  
	 			else{//����������Ϣ
	 				socketfu.send(ss);
	 				sta.append(ss+"\n");
	 				System.out.println(ss);
	 				sta.setCaretPosition(sta.getText().length());//�����������ʾ��λ
	 			}	 				
	 				 } 

	 	} catch (IOException e) {
	 		clients.removeByValue(ps);
	 	}
	 }
}
 class Map<K,V> extends HashMap<K,V> {//������map����һ���û���Socket��ӳ���ϵ
		// ����value��ɾ��ָ����
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
		// ��ȡvalue����
		public Set<V> valueSet()
		{
			Set<V> result=new HashSet<V>();
			for(Object key : keySet())
			{
				result.add(get(key));
			}
			return result;
		}
		// ��дHashMap��put�������÷���������value�ظ�
		public V put(K key,V value)
		{
			for(V val : valueSet())
			{
				if(val==value||val.equals(value))
				{
					throw new RuntimeException("���������ظ�value");
				}
			}
			return super.put(key, value);	
		}
		
		// ͨ��value����key
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
	 (new server("������")).getserver();
	 
 }
}
