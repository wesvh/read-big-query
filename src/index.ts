import ReadBigQueryModule from "./ReadBigQueryModule";

export async function readDictionary(value: string) {
  return await ReadBigQueryModule.readDictionary(value);
}

export async function PetitionInKotlin(value: string) {
  return await ReadBigQueryModule.PetitionInKotlin(value);
}
