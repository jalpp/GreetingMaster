package org.example;


import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bson.Document;

import java.awt.*;
import java.io.IOException;


public class Main extends ListenerAdapter {

    private static JDA jda;

    public static MongoCollection<Document> collection;

    public AntiSpam gnspam = new AntiSpam(86400000, 1);

    public AntiSpam gmspam = new AntiSpam(86400000, 1);

    public AntiSpam gaspam = new AntiSpam(86400000, 1);

    public static void main(String[] args) {

        JDABuilder jdaBuilder = JDABuilder.createDefault(Keystore.Discord).enableIntents(GatewayIntent.GUILD_MEMBERS);
        jdaBuilder.disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOJI, CacheFlag.STICKER, CacheFlag.SCHEDULED_EVENTS);

        jdaBuilder.setStatus(OnlineStatus.ONLINE);

        jdaBuilder.setActivity(Activity.playing("good morning!"));

        jdaBuilder.addEventListeners(new Main());

        try {
            jda = jdaBuilder.build();

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(Commands.slash("greet", "greet the world").addOptions(
                new OptionData(OptionType.STRING, "type", "choose greet type", true)
                        .addChoice("Good Morning", "gm")
                        .addChoice("Good Night", "gn")
                        .addChoice("Good Afternoon", "gn")
                ));

        commands.addCommands(Commands.slash("top10","View top 10 greet masters"));

        commands.addCommands(Commands.slash("greethelp", "View helpful info"));

        commands.addCommands(Commands.slash("profile", "View your greetings profile"));

        commands.queue();



        String connectionString = Keystore.mongo;

        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);

        MongoDatabase database = mongoClient.getDatabase(Keystore.databaseName);

        collection =  database.getCollection(Keystore.collectionName);


    }




    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
      GreetLogic logic = new GreetLogic();
      switch (event.getName()){
          case "greet" -> {
              switch (event.getOptionsByName("type").get(0).getAsString()){
                  case "gm" ->{
                      if(!gmspam.checkSpam(event)){
                          if(logic.userPresentNormalGreet(collection, event.getUser().getId())){
                              logic.updatePlayer(event.getUser().getId(), 1, Greetings.GOOD_MORNING.toString(), collection);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_MORNING.getProperName() + "! " + GreetLogic.morn + " " + Greetings.GOOD_MORNING.enduserMsgConverter() + " greet master count updated!").queue();
                          }else{
                              Document doc = new Document("Discordid", event.getUser().getId())
                                      .append("name", event.getUser().getName())
                                      .append(Greetings.GOOD_MORNING.toString(), 1)
                                      .append(Greetings.GOOD_NIGHT.toString(), 0)
                                      .append(Greetings.GOOD_AFTERNOON.toString(), 0)
                                      ;
                              collection.insertOne(doc);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_MORNING.getProperName() + GreetLogic.morn + " and has entered the greeting race!" + Greetings.GOOD_MORNING.enduserMsgConverter() + " greet master count updated!").queue();
                          }
                      }else{
                          event.reply("You said" + Greetings.GOOD_MORNING.getProperName() + "already! try other greetings or just take some rest!").queue();
                      }
                  }

                  case "ga" ->{
                      if(!gaspam.checkSpam(event)){
                          if(logic.userPresentNormalGreet(collection, event.getUser().getId())){
                              logic.updatePlayer(event.getUser().getId(), 1, Greetings.GOOD_AFTERNOON.toString(), collection);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_AFTERNOON.getProperName() + " " + GreetLogic.af + " " + Greetings.GOOD_AFTERNOON.enduserMsgConverter() +  " greet master count updated!").queue();
                          }else{
                              Document doc = new Document("Discordid", event.getUser().getId())
                                      .append("name", event.getUser().getName())
                                      .append(Greetings.GOOD_MORNING.toString(), 0)
                                      .append(Greetings.GOOD_NIGHT.toString(), 0)
                                      .append(Greetings.GOOD_NIGHT.toString(), 1)
                                      ;
                              collection.insertOne(doc);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_AFTERNOON.getProperName() + " " + GreetLogic.af +" and has entered the greeting race!" + " " + Greetings.GOOD_AFTERNOON.enduserMsgConverter() + " greet master count updated!").queue();
                          }
                      }else{
                          event.reply("You said" + Greetings.GOOD_AFTERNOON.getProperName() + " already! try other greetings or just take some rest!").queue();
                      }

                  }
                  case "gn" ->{

                      if(!gnspam.checkSpam(event)){
                          if(logic.userPresentNormalGreet(collection, event.getUser().getId())){
                              logic.updatePlayer(event.getUser().getId(), 1, Greetings.GOOD_NIGHT.toString(), collection);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_NIGHT.getProperName() + " " + GreetLogic.night + " " + Greetings.GOOD_NIGHT.enduserMsgConverter() + " greet master count updated!").queue();
                          }else{
                              Document doc = new Document("Discordid", event.getUser().getId())
                                      .append("name", event.getUser().getName())
                                      .append(Greetings.GOOD_MORNING.toString(), 0)
                                      .append(Greetings.GOOD_NIGHT.toString(), 1)
                                      .append(Greetings.GOOD_AFTERNOON.toString(), 0)
                                      ;
                              collection.insertOne(doc);
                              event.reply(event.getUser().getName() + " said " + Greetings.GOOD_NIGHT.getProperName() + " " + GreetLogic.night + " and has entered the greeting race! " + Greetings.GOOD_NIGHT.enduserMsgConverter() + " greet master count updated!").queue();
                          }
                      }else{
                          event.reply("You said " + Greetings.GOOD_NIGHT.getProperName() + " already! try other greetings or just take some rest!").queue();
                      }

                  }
              }
          }

          case "top10" -> event.replyEmbeds(new EmbedBuilder().setDescription("**Rank Username GN score GA score GM score**\n\n" + logic.getTop10Players(collection)).setTitle("Top 10 Greet Masters").setThumbnail("https://cdn-icons-png.flaticon.com/128/1426/1426727.png").setColor(Color.YELLOW).build()).addActionRow(Button.primary("count", "\uD83D\uDC64 Player Count: " + collection.countDocuments()).asDisabled()).addActionRow(Button.primary("help", "❓")).queue();

          case "greethelp" -> event.reply("**GreetingMaster Help Guide**" +
                  "\n" +
                  "**/greet** Greet good morning, night, and afternoon in any server within 1 day, max greeting per day are 3" +
                  "\n**/top10** View top 10 Greet Masters around the world (all discords)" +
                  "\n**/profile** View your greeting profile" +
                  "\n**/greethelp** View GreetMaster bot commands").setEphemeral(true).queue();


          case "profile" -> event.replyEmbeds(logic.castProfileDirectly(event, collection).build()).queue();


      }


    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        if(event.getComponentId().equalsIgnoreCase("help")){
            event.reply("**Greet Masters Leaderboard Guide** \n Players score greet points everyday by running **/greet** command, they can score max of 3 points per day (in the whole day). Leaderboard resets every year at Sep, 1st.").setEphemeral(true).queue();
        }

    }
}