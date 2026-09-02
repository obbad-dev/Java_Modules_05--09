package fr.s42.chat.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Optional;
import fr.s42.chat.models.*;
import fr.s42.chat.exceptions.*;

import javax.sql.DataSource;

public class MessagesRepositoryJdbcImpl implements MessagesRepository {
	private final DataSource dataSource;

	public MessagesRepositoryJdbcImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Optional<Message> findById(Long id) {

		String SQL_QUERY = "SELECT * FROM message JOIN users ON message.author_id = users.user_id INNER JOIN chatroom ON message.chatroom_id = chatroom.chatroom_id WHERE message.message_id = ?;";

		try (Connection con = dataSource.getConnection();
				PreparedStatement ps = con.prepareStatement(SQL_QUERY)) {
			ps.setLong(1, id);
			try (ResultSet res = ps.executeQuery();) {
				if (res.next()) {
					// 1. Build the User (Author)
					User user = new User();
					user.setId(res.getLong("user_id"));
					user.setLogin(res.getString("login"));
					user.setPassword(res.getString("password"));

					// 2. Build the Chatroom
					Chatroom room = new Chatroom();
					room.setId(res.getLong("chatroom_id"));
					room.setName(res.getString("name"));

					// 3. Build the Message
					Message message = new Message();
					message.setId(res.getLong("message_id"));
					message.setAuthor(user);
					message.setRoom(room); // Fixed: We attached the room!

					// Check your DB schema: is the column named "text" or "message"?
					message.setText(res.getString("text"));
					
					// Fixed: Get the Timestamp and convert it to Java's LocalDateTime
					message.setDateTime(LocalDateTime.parse(res.getString("date_time")));

					// 4. Return the finished product!
					return Optional.of(message);
				} else
					System.out.println("the ID doesn't exist");

			}

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return Optional.empty();
	}

	@Override
	public void save(Message message) {

		if (message == null)
			return;
		if (message.getAuthor() == null || message.getAuthor().getId() == null)
			throw new NotSavedSubEntityException("Error: author not found!");
		if (message.getRoom() == null || message.getRoom().getId() == null)
			throw new NotSavedSubEntityException("Error: room not found!");

		String SQL_QUERY = "INSERT INTO "
						+"message(author_id, chatroom_id, text, date_time) "
						+"VALUES (?,?,?,?);";
		try (Connection con = dataSource.getConnection();
	        PreparedStatement pre = con.prepareStatement(SQL_QUERY, java.sql.Statement.RETURN_GENERATED_KEYS)) {
			pre.setLong(1, message.getAuthor().getId());
			pre.setLong(2, message.getRoom().getId());
			pre.setString(3, message.getText());
			pre.setTimestamp(4, java.sql.Timestamp.valueOf(message.getDateTime()));
			pre.executeUpdate();
			ResultSet rs = pre.getGeneratedKeys();
			if (rs.next())
				message.setId(rs.getLong(1));
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
				
	}

}
