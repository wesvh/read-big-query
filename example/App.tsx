import { StyleSheet, Text, View, TouchableOpacity } from "react-native";

import * as ReadBigQuery from "read-big-query";
import ArrayMock from "./ArrayMock";

export default function App() {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        onPress={async () => {
          const performanced = performance.now();
          const zona = "10013";
          const sitio = "020";
          const allProductsData: any = []; // Este array almacenará todos los datos de los productos

          try {
            let finished = false;
            let blockStart = 0;

            while (!finished) {
              const steps = Array.from(
                { length: 10 },
                (_, i) => blockStart + i
              );
              const requests = steps.map((step) =>
                ReadBigQuery.PetitionInKotlin(
                  `https://tradego-suite-backend-qa.altipal.com.co/api/Product/briefcase/${zona}/${sitio}?page=${step}`
                )
              );
              console.log(
                `0 El tiempo de ejecución total es de : ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
              );
              const productsPages = await Promise.all(requests);

              console.log(
                `1 El tiempo de ejecución total es de : ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
              );

              for (const [index, prods] of productsPages.entries()) {
                const currentPage = blockStart + index;
                const prodsFormatted = JSON.parse(prods);
                if (prodsFormatted && prodsFormatted.length > 0) {
                  Array.prototype.push.apply(allProductsData, prodsFormatted);
                }

                // Si es la última página del bloque y no hay datos, terminar.
                if (currentPage >= 9 && prodsFormatted.length === 0) {
                  console.log("No hay datos en la página", currentPage);
                  finished = true;
                }
              }

              // Prepararse para el siguiente bloque si es necesario.
              if (!finished) {
                blockStart += 5;
              }
            }

            // Después de procesar todas las páginas, realizar una única llamada a `createStock`
            if (allProductsData.length > 0) {
            }
          } catch (error) {
            console.error(
              "Error al procesar los productos o restricciones del proveedor",
              error
            );
          } finally {
            console.log(
              `El tiempo de ejecución total es de : ${((performance.now() - performanced) / 1000).toFixed(2)}s.`
            );
          }
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
