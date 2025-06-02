package Data;
import model.GameData;
import java.util.Collection;

public record ListGameResponse(Collection<GameData> games) {
}
