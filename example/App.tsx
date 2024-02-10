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
          const JSONSTRING = JSON.stringify([
            ["a", "b", "c"],
            ["d", "e", "f"],
            ["g", "h", "i"],
          ]);
          console.log(
            `El tiempo de ejecución fue de ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
          );
          await ReadBigQuery.readDictionary(JSONSTRING);
          console.log(
            `2 El tiempo de ejecución fue de ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
          );
          console.log("Finalizo");
        }}
      >
        <Text>Open up App.tssssx to start working on your app!</Text>
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
