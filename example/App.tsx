import { StyleSheet, Text, View, TouchableOpacity } from "react-native";

import * as ReadBigQuery from "read-big-query";
import ArrayMock from "./ArrayMock";

export default function App() {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={async () => {
          const performanced = performance.now();
          console.log("EXECUTING");
          const JSONSTRING = JSON.stringify(ArrayMock);
          console.log(
            `El tiempo de ejecución fue de ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
          );
          console.log("typeof JSONSTRING", typeof JSONSTRING);
          ReadBigQuery.readDictionary(JSONSTRING)
            .then((result) => {
              console.log("result", typeof result);
              const parsedJSON = JSON.parse(result);
              console.log("parsedJSON", typeof parsedJSON);
            })
            .catch((error) => console.error(error))
            .finally(() =>
              console.log(
                `2 El tiempo de ejecución fue de ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
              )
            );
        }}
      >
        <View
          style={{
            //style like a pressableButton
            backgroundColor: "blue",
            padding: 10,
            borderRadius: 10,
            margin: 10,
          }}
        >
          <Text>Open up App.tssssx to start working on your app!</Text>
        </View>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
    alignItems: "center",
    justifyContent: "center",
  },
});
