
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Base64;
import java.util.List;

import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.Fields;
import com.caixabank.absis.apps.dataservice.poc.fastdata.domain.value.ValueSchema;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadJsonData {
	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		// String jsonData =
		// "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"int32\",\"optional\":false,\"field\":\"id\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Timestamp\",\"version\":1,\"field\":\"fecha_ultima_modificacion\"}],\"optional\":false,\"name\":\"v_expedientes\"},\"payload\":{\"id\":4,\"fecha_ultima_modificacion\":1744567685000}}";
		String expediente = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"int32\",\"optional\":false,\"field\":\"id\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"fecha_contratacion\"},{\"type\":\"string\",\"optional\":false,\"field\":\"titular\"},{\"type\":\"string\",\"optional\":false,\"field\":\"numero_expediente\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Timestamp\",\"version\":1,\"field\":\"fecha_ultima_modificacion\"}],\"optional\":false,\"name\":\"expedientes\"},\"payload\":{\"id\":7,\"fecha_contratacion\":20161,\"titular\":\"Zorra Pelona3\",\"numero_expediente\":\"EXP-006\",\"fecha_ultima_modificacion\":1744648810000}}";
		showData(expediente);
		String traspaso = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"int32\",\"optional\":false,\"field\":\"id\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"id_expediente\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"id_fondo_origen\"},{\"type\":\"int32\",\"optional\":false,\"field\":\"id_fondo_destino\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"fecha\"},{\"type\":\"bytes\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Decimal\",\"version\":1,\"field\":\"importe\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Timestamp\",\"version\":1,\"field\":\"fecha_ultima_modificacion\"}],\"optional\":false,\"name\":\"traspasos\"},\"payload\":{\"id\":1,\"id_expediente\":1,\"id_fondo_origen\":1,\"id_fondo_destino\":2,\"fecha\":20134,\"importe\":\"TEtA\",\"fecha_ultima_modificacion\":1745841255000}}";
		showData(traspaso);
		String fondo = "{\"schema\":{\"type\":\"struct\",\"fields\":[{\"type\":\"int32\",\"optional\":false,\"field\":\"id\"},{\"type\":\"string\",\"optional\":false,\"field\":\"nombre\"},{\"type\":\"string\",\"optional\":false,\"field\":\"tipo\"},{\"type\":\"int32\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Date\",\"version\":1,\"field\":\"fecha_creacion\"},{\"type\":\"bytes\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Decimal\",\"version\":1,\"field\":\"volumen_activos\"},{\"type\":\"int64\",\"optional\":false,\"name\":\"org.apache.kafka.connect.data.Timestamp\",\"version\":1,\"field\":\"fecha_ultima_modificacion\"}],\"optional\":false,\"name\":\"fondos\"},\"payload\":{\"id\":1,\"nombre\":\"Fondo A\",\"tipo\":\"Equity\",\"fecha_creacion\":18262,\"volumen_activos\":\"Hc1lAQ==\",\"fecha_ultima_modificacion\":1746720974000}}";
		showData(fondo);
	}

	private static void showData(String message) throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		ValueSchema data = mapper.readValue(message, ValueSchema.class);

		String staff2PrettyPrint = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);

		System.out.println(staff2PrettyPrint);

		JsonNode node = mapper.readTree(message);
		JsonNode payload = node.get("payload");
//        System.out.println(node.toPrettyString());
//        System.out.println(payload.toPrettyString());

		String tableName = data.getSchema().getName();

		System.out.println("Table: " + tableName);
		System.out.println("Columns");
		List<Fields> fields = data.getSchema().getFields();
		for (Fields field : fields) {
			JsonNode node2 = payload.get(field.getField());
			if (node2 != null) {
				if (field.getName() != null && field.getName().equals("org.apache.kafka.connect.data.Decimal")) {
					BigDecimal decoded = new BigDecimal(new BigInteger(Base64.getDecoder().decode(field.getField())), 2);
					System.out.println("\t" + field.getField() + ": " + decoded);
				} else {
					System.out.println("\t" + field.getField() + ": " + node2.asText());
				}
			}
		}
	}
}
