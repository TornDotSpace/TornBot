import java.util.HashMap;
import java.util.Scanner;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import java.util.List;


public class Manager {
	public static HashMap<String,String> truthRank = new HashMap<>();
	public static HashMap<String,String> truthSide = new HashMap<>();

	public static Guild cont = DiscordBot.jda.getGuildById("247490958374076416");
	
	public static TextChannel bots = cont.getTextChannelById("493489353046097926");
	public static TextChannel gen = cont.getTextChannelById("247490958374076416");
	
	public static String[] roles = {"453678967996678145", "453678938275708960", "453678890628546560", "453678855534804992", "453612904365948929", "453620521632923660", "453620581041045555", "453620631116709888", "453620675526000674", "453620720581214208"};
	public static String[] rolesHA = {"513781861542002690", "524288679473184806"};

	public static void tornUpdate(){
		sendMessage("Updating roles...",bots);
		
		getTruth();

		int ct = 0;
		//Update each player
		List<Member> members = cont.getMembers();
		for(Member m : members) {
			int o = updateOnePlayer(m);
			if(o == 1) System.out.println("Updated " + m.getEffectiveName());
			ct += o;
		}
		
		sendMessage(ct + " players updated.", bots);
	}
	
	public static int updateOnePlayer(Member m) {
		int o = 0;
		if(m.getUser().isBot()) return 0;
		
		String name = m.getEffectiveName().toLowerCase();
		String correctRank = truthRank.get(name);
		String correctSide = truthSide.get(name);
		
		//remove bad roles
		boolean hasRank = false;
		boolean hasSide = false;
		for(Role r : m.getRoles()) {
			if(contains(roles,r.getId())) { // dont remove any non-rank roles
				if(correctRank == null || !correctRank.equals(r.getId())) {cont.removeRoleFromMember(m, r).complete(); o=1;}
				else hasRank = true;
			}
			if(contains(rolesHA,r.getId())) { // dont remove any non-rank roles
				if(correctSide == null || !correctSide.equals(r.getId())) {cont.removeRoleFromMember(m, r).complete(); o=1;}
				else hasSide = true;
			}
		}
		if(correctRank == "" || correctSide == "" || correctRank == null || correctSide == null) return o;
		
		if(!hasRank) {cont.addRoleToMember(m, cont.getRoleById(correctRank)).complete(); o=1;}
		if(!hasSide) {cont.addRoleToMember(m, cont.getRoleById(correctSide)).complete(); o=1;}
		
		return o;
	}
	
	private static boolean contains(String[] arr, String s) {
		for(String here : arr) if(here.equals(s)) return true;
		return false;
	}

	public static void getTruth() {
		System.out.println("Getting truth...");
		String doc = Web.getWebInfo("https://torn.space/leaderboard");
		String[] spl = doc.split("</tr>");
		truthRank.clear();
		truthSide.clear();
		
		for(int i = 1; i < spl.length - 1; i++){
			//Initialize place and name
			String color = spl[i].substring(0,28);
			spl[i] = spl[i].substring(28, spl[i].length()-5).replaceAll("\\.", "").replaceAll("<", " <");
			Scanner sc = new Scanner(spl[i]);
			int place = sc.nextInt();
			spl[i] = spl[i].replace(place+"", "").substring(11);
			String name = spl[i];
			if(name.charAt(0) == '[') name = name.split(" ")[1];
			else name = name.split(" ")[0];
			sc.close();
			
			String rank = "";
			if(place <= 5) rank = roles[9];
			else if(place <= 10) rank = roles[8];
			else if(place <= 25) rank = roles[7];
			else if(place <= 50) rank = roles[6];
			else if(place <= 75) rank = roles[5];
			else if(place <= 100) rank = roles[4];
			else if(place <= 250) rank = roles[3];
			else if(place <= 500) rank = roles[2];
			else if(place <= 750) rank = roles[1];
			else if(place <= 1000) rank = roles[0];
			truthRank.put(name, rank);
			
			String side = "";
			if(color.contains("pink")) side = rolesHA[1];
			else if(color.contains("cyan")) side = rolesHA[0];
			if(side.length() > 0) truthSide.put(name, side);
		}
		System.out.println("Got truth");
	}
	
	public static void sendMessage(String msg, TextChannel tc) {
		System.out.println(msg);
		tc.sendMessage(msg).queue();
	}
}
